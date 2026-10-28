package com.hrm.api;

import com.hrm.util.Config;
import com.hrm.util.Log;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * OrangeHRM API Test Suite
 *
 * Exercises Symfony HTTP endpoints used by the OrangeHRM 4.0 web application.
 * All tests skip gracefully if the local server is not running.
 */
public class OrangeHRMApiTest {

    private String baseUrl;
    private java.util.Map<String, String> sessionCookies = new java.util.HashMap<>();

    @BeforeClass
    public void setupApiBase() {
        baseUrl = Config.getProperty("app.url.local");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost/orangehrm/symfony/web/index.php";
        }
        if (baseUrl.endsWith("/auth/login")) {
            baseUrl = baseUrl.replace("/auth/login", "");
        }
        try {
            java.net.URI uri = java.net.URI.create(baseUrl);
            String hostPart = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "");
            String pathPart = uri.getPath();
            RestAssured.baseURI = hostPart;
            RestAssured.basePath = (pathPart != null && !pathPart.isEmpty()) ? pathPart : "/orangehrm/symfony/web/index.php";
        } catch (Exception e) {
            RestAssured.baseURI = "http://localhost";
            RestAssured.basePath = "/orangehrm/symfony/web/index.php";
        }
        RestAssured.useRelaxedHTTPSValidation();
        Log.info("[API] baseURI: " + RestAssured.baseURI + " | basePath: " + RestAssured.basePath);

        try {
            Response ping = given().relaxedHTTPSValidation()
                    .when().get("/auth/login").then().extract().response();
            if (ping.getStatusCode() != 200) {
                throw new SkipException("OrangeHRM returned HTTP " + ping.getStatusCode() +
                        ". Start XAMPP and re-run.");
            }
            Log.info("[API] Server reachable HTTP " + ping.getStatusCode());
        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            throw new SkipException("OrangeHRM not running at " + baseUrl +
                    ". Start XAMPP first. Error: " + e.getMessage());
        }
        sessionCookies = performLogin();
    }

    /** TC-API-01: Login page returns HTTP 200 */
    @Test(description = "API-01: Login page is reachable (HTTP 200)", priority = 1)
    public void tc01_loginPageReturns200() {
        Log.startTestCase("TC-API-01: Login page HTTP 200");
        Response r = given().relaxedHTTPSValidation()
                .when().get("/auth/login").then().extract().response();
        Log.info("[API] GET /auth/login => " + r.getStatusCode());
        Assert.assertEquals(r.getStatusCode(), 200,
                "Login page must return HTTP 200");
        Assert.assertTrue(r.asString().contains("OrangeHRM") || r.asString().contains("txtUsername"),
                "Response must contain OrangeHRM branding or login form");
        Log.info("[API] TC-API-01 PASSED");
        Log.endTestCase();
    }

    /** TC-API-02: Valid credentials POST returns success response */
    @Test(description = "API-02: Valid credentials POST returns 200 or 302", priority = 2)
    public void tc02_loginWithValidCredentials() {
        Log.startTestCase("TC-API-02: Valid credentials login");
        Response loginPage = given().relaxedHTTPSValidation()
                .when().get("/auth/login").then().extract().response();
        String csrf = extractCsrfToken(loginPage.asString());
        String initCookie = loginPage.getCookie("symfony");

        RequestSpecification req = given().relaxedHTTPSValidation()
                .contentType(ContentType.URLENC)
                .formParam("txtUsername", Config.getProperty("username"))
                .formParam("txtPassword", Config.getProperty("password"))
                .formParam("Submit", "LOGIN");
        if (csrf != null) req = req.formParam("_csrf_token", csrf);
        if (initCookie != null) req = req.cookie("symfony", initCookie);

        Response r = req.when().post("/auth/validateCredentials")
                .then().extract().response();
        int code = r.getStatusCode();
        Log.info("[API] POST /auth/validateCredentials => " + code);
        Assert.assertTrue(code == 200 || code == 302,
                "Valid login POST must return 200 or 302, got: " + code);
        String loc = r.getHeader("Location");
        boolean ok = (code == 302 && loc != null) || (code == 200);
        Assert.assertTrue(ok, "Response must be a redirect or 200 page after valid login");
        Log.info("[API] TC-API-02 PASSED");
        Log.endTestCase();
    }

    /** TC-API-03: Invalid credentials POST does NOT redirect to dashboard */
    @Test(description = "API-03: Invalid credentials are rejected", priority = 3)
    public void tc03_loginWithInvalidCredentials() {
        Log.startTestCase("TC-API-03: Invalid credentials rejected");
        Response loginPage = given().relaxedHTTPSValidation()
                .when().get("/auth/login").then().extract().response();
        String csrf = extractCsrfToken(loginPage.asString());
        String initCookie = loginPage.getCookie("symfony");

        RequestSpecification req = given().relaxedHTTPSValidation()
                .contentType(ContentType.URLENC)
                .formParam("txtUsername", "bad_user_xyz_9999")
                .formParam("txtPassword", "wrong_pass_abc_123")
                .formParam("Submit", "LOGIN");
        if (csrf != null) req = req.formParam("_csrf_token", csrf);
        if (initCookie != null) req = req.cookie("symfony", initCookie);

        Response r = req.when().post("/auth/validateCredentials")
                .then().extract().response();
        int code = r.getStatusCode();
        String loc = r.getHeader("Location");
        Log.info("[API] POST with invalid creds => HTTP " + code + " | Location: " + loc);

        boolean redirectsToDashboard = loc != null && loc.contains("dashboard");
        Assert.assertFalse(redirectsToDashboard,
                "Invalid credentials must NOT redirect to dashboard");
        Log.info("[API] TC-API-03 PASSED");
        Log.endTestCase();
    }

    /** TC-API-04: Authenticated session can access dashboard */
    @Test(description = "API-04: Authenticated session accesses dashboard (200 or 302)",
          priority = 4, dependsOnMethods = "tc02_loginWithValidCredentials")
    public void tc04_authenticatedDashboardAccess() {
        Log.startTestCase("TC-API-04: Authenticated dashboard access");
        if (sessionCookies == null || sessionCookies.isEmpty()) {
            throw new SkipException("Session cookies not available; skipping authenticated test");
        }
        Response r = given().relaxedHTTPSValidation()
                .cookies(sessionCookies)
                .redirects().follow(false)
                .when().get("/dashboard")
                .then().extract().response();
        int code = r.getStatusCode();
        Log.info("[API] GET /dashboard (auth) => " + code);
        Assert.assertTrue(code == 200 || code == 302,
                "Authenticated dashboard GET must return 200 or 302, got: " + code);
        Log.info("[API] TC-API-04 PASSED");
        Log.endTestCase();
    }

    /** TC-API-05: Unauthenticated request to admin page redirects to login */
    @Test(description = "API-05: Unauthenticated admin access redirects to login",
          priority = 5)
    public void tc05_unauthenticatedAccessRedirectsToLogin() {
        Log.startTestCase("TC-API-05: Unauthenticated redirect check");
        Response r = given().relaxedHTTPSValidation()
                .redirects().follow(false)
                .when().get("/admin/viewAdminModule")
                .then().extract().response();
        int code = r.getStatusCode();
        String loc = r.getHeader("Location");
        Log.info("[API] GET /admin/viewAdminModule (no auth) => HTTP " + code + " | Location: " + loc);

        boolean redirectToLogin = (code == 302 || code == 301)
                && loc != null && (loc.contains("login") || loc.contains("auth"));
        boolean showsLoginPage = (code == 200) && r.asString().contains("txtUsername");

        Assert.assertTrue(redirectToLogin || showsLoginPage,
                "Unauthenticated access must redirect to login. Got HTTP " + code + ", Location: " + loc);
        Log.info("[API] TC-API-05 PASSED");
        Log.endTestCase();
    }

    // ---- helpers ----
    private java.util.Map<String, String> performLogin() {
        try {
            Response lp = given().relaxedHTTPSValidation()
                    .when().get("/auth/login").then().extract().response();
            String csrf = extractCsrfToken(lp.asString());
            java.util.Map<String, String> initCookies = lp.getCookies();
            RequestSpecification req = given().relaxedHTTPSValidation()
                    .contentType(ContentType.URLENC)
                    .formParam("txtUsername", Config.getProperty("username"))
                    .formParam("txtPassword", Config.getProperty("password"))
                    .formParam("Submit", "LOGIN");
            if (csrf != null) req = req.formParam("_csrf_token", csrf);
            if (initCookies != null && !initCookies.isEmpty()) req = req.cookies(initCookies);
            Response r = req.redirects().follow(false).when().post("/auth/validateCredentials").then().extract().response();
            java.util.Map<String, String> cookies = new java.util.HashMap<>(initCookies);
            cookies.putAll(r.getCookies());
            Log.info("[API] Session login => HTTP " + r.getStatusCode() + " | cookies: " + cookies.keySet());
            return cookies;
        } catch (Exception e) {
            Log.warn("[API] performLogin failed: " + e.getMessage());
            return java.util.Collections.emptyMap();
        }
    }

    private String extractCsrfToken(String html) {
        try {
            String search = "name=\"_csrf_token\" value=\"";
            int s = html.indexOf(search);
            if (s == -1) return null;
            s += search.length();
            int e = html.indexOf("\"", s);
            return (e > s) ? html.substring(s, e) : null;
        } catch (Exception e) { return null; }
    }
}
