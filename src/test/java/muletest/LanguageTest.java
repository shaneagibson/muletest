package muletest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Rule;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class LanguageTest extends FunctionalTestCase {

    @Override
    protected String getConfigFile() {
        return "src/main/app/mule-config.xml";
    }

    @Rule
    public WireMockRule errorService = new WireMockRule(9000);

    @Test
    public void shouldRespondInEnglish() {
        assertResponseForLanguage("English", "Hello!");
    }

    @Test
    public void shouldRespondInFrench() {
        assertResponseForLanguage("French", "Bonjour!");
    }

    @Test
    public void shouldRespondInSpanish() {
        assertResponseForLanguage("Spanish", "Hola!");
    }

    @Test
    public void shouldRespondInEnglishByDefaultWhenLanguageIsEmpty() {
        stubErrorServiceForUnknownLanguage("");
        assertResponseForLanguage("", "Hello!");
        verifyErrorServiceInvokedForUnknownLanguage("");
    }

    @Test
    public void shouldRespondInEnglishByDefaultWhenLanguageIsUnknown() {
        stubErrorServiceForUnknownLanguage("Blah");
        assertResponseForLanguage("Blah", "Hello!");
        verifyErrorServiceInvokedForUnknownLanguage("Blah");
    }

    private void assertResponseForLanguage(final String language, final String expectedResponse) {
        try {
            final HttpResponse<String> response = Unirest.get("http://localhost:8081/?language=" + language).asString();
            assertEquals(200, response.getStatus());
            assertEquals(expectedResponse, response.getBody());
        } catch (final UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private void stubErrorServiceForUnknownLanguage(final String language) {
        errorService.stubFor(
                post(urlPathEqualTo("/error"))
                        .withQueryParam("error", equalTo(urlEncode("Unknown language: "+language)))
                        .willReturn(aResponse().withStatus(200)));
    }

    private void verifyErrorServiceInvokedForUnknownLanguage(final String language) {
        errorService.verify(1, postRequestedFor(urlPathEqualTo("/error"))
                .withQueryParam("error", equalTo("Unknown language: "+language))
        );
    }


    private String urlEncode(final String string) {
        try {
            return URLEncoder.encode(string, "UTF-8").replace("+", "%20");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}