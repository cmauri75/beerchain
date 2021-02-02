package net.cmauri.chain;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;


/**
 * This class relies on 3 nodes started on port 8010,8011,8012 by docker compose
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChainControllerTest {
    //private final static String node1IntUrl = "http://birchain-node1:8888";
    private final static String node2IntUrl = "http://birchain-node2:8888";
    private final static String node3IntUrl = "http://birchain-node3:8888";
    private final static String node1Url = "http://localhost:8010";
    private final static String node2Url = "http://localhost:8011";
    private final static String node3Url = "http://localhost:8012";
    private final static String trans1Json = "{ \n" +
            "\t\"amount\": 30,\n" +
            "\t\"sender\": \"CMAURI#9991\",\n" +
            "\t\"recipient\" : \"FRA#00012188\"\n" +
            "}";

    @BeforeAll
    public static void setup() {
    }

    private void clearNode(String nodeURI) {
        RestAssured.baseURI = nodeURI;
        given()
                .urlEncodingEnabled(true)
                .param("clearNet", true)
                .when()
                .post("/restart")
                .then()
                .extract().response();
    }

    /**
     * Clears all node chains, verify get information from blockchain, it should be empty
     */
    @Test
    @Order(1)
    public void GetStateZero() {
        clearNode(node1Url);
        clearNode(node2Url);
        clearNode(node3Url);

        RestAssured.baseURI = node1Url;
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[]", response.jsonPath().getString("networkNodes"));
    }

    /**
     * Registers node2 to node1, node3 to node2 and verify node3 contains both node2 and node1 url
     */
    @Test
    @Order(2)
    public void checkNodeRegistration() {
        RestAssured.baseURI = node1Url;
        Response response = given()
                .urlEncodingEnabled(true)
                .param("newNodeUrl", node2IntUrl)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .post("/register-and-broadcast-node")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        RestAssured.baseURI = node2Url;
        response = given()
                .urlEncodingEnabled(true)
                .param("newNodeUrl", node3IntUrl)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .post("/register-and-broadcast-node")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());


        RestAssured.baseURI = node3Url;
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[http://birchain-node2:8888, http://birchain-node1:8888]", response.jsonPath().getString("networkNodes"));
    }

    /**
     * Creates a transaction in node1 and verify it's correct inserted in pending one of node2
     */
    @Test
    @Order(3)
    public void createTransactions() {
        RestAssured.baseURI = node1Url;
        Response response = given()
                .urlEncodingEnabled(true)
                .body(trans1Json)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .header("Content-type", ContentType.JSON)
                .post("/transaction/broadcast")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        RestAssured.baseURI = node2Url;
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("30", response.jsonPath().getString("chain.pendingTransactions[0].amount"));
        Assertions.assertEquals("CMAURI#9991", response.jsonPath().getString("chain.pendingTransactions[0].sender"));
        Assertions.assertEquals("FRA#00012188", response.jsonPath().getString("chain.pendingTransactions[0].recipient"));
    }

    /**
     * Mines a block in node2 and verify node3 contains mined block with valid hash
     */
    @Test
    @Order(4)
    public void mineNewBlock() {
        RestAssured.baseURI = node2Url;
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/mine")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        RestAssured.baseURI = node3Url;
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("35978", response.jsonPath().getString("chain.blockList[1].nonce"));
        Assertions.assertTrue(response.jsonPath().getString("chain.blockList[1].hash").startsWith(Birchain.hashCodeStarter));
    }

    /**
     * restarts node1, verify it's empty, call consensus, verify block hash been received correctly
     */
    @Test
    @Order(5)
    public void z_consensus() {
        RestAssured.baseURI = node1Url;
        given()
                .urlEncodingEnabled(true)
                .param("clearNet", false)
                .when()
                .post("/restart")
                .then()
                .extract().response();

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("1", response.jsonPath().getString("chain.chainSize"));
        Assertions.assertEquals("[]", response.jsonPath().getString("chain.pendingTransactions"));

        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/consensus")
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("New chain got from remote chain", response.asString() );

        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/blockchain")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("2", response.jsonPath().getString("chain.chainSize"));

        Assertions.assertEquals("12.5", response.jsonPath().getString("chain.pendingTransactions[0].amount"));
        Assertions.assertEquals("00", response.jsonPath().getString("chain.pendingTransactions[0].sender"));
        Assertions.assertEquals("nodeAddr", response.jsonPath().getString("chain.pendingTransactions[0].recipient"));
    }


}
