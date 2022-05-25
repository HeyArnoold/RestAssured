package api;

import api.pogos.Register;
import api.pogos.SuccessReg;
import api.pogos.UnSuccessReg;
import api.pogos.UserData;
import api.specifications.Specifications;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class ReqresTest {

    private static final String URL = "https://reqres.in/";
    private static final String RegisterURL = "api/register";

    @Test
    public void checkAvatarAndId() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        List<UserData> users = given()  // тип запроса GET
                .when()
                .get("api/users?page=2") // тип запроса GET
                .then().log().all() // делаем выгрузку в консоль с помощью log.all
                .extract().jsonPath().getList("data", UserData.class);

        users.forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));
        Assertions.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
    }

    @Test
    public void successRegTest() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register newUser = new Register("eve.holt@reqres.in", "pistol");

        SuccessReg successReg = given()
                .body(newUser)
                .when()
                .post(RegisterURL)
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessRegTest() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        Register userWithoutPass = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(userWithoutPass)
                .when()
                .post(RegisterURL)
                .then().log().all()
                .extract().as(UnSuccessReg.class);

        Assertions.assertEquals("Missing password", unSuccessReg.getError());
    }

    @Test
    public void checkByName() {
        String s = given()
                .baseUri("https://api.hh.ru/")
                .basePath("vacancies/55883714")
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .extract().jsonPath().getString("description");

        System.out.println(s);

    }
}
