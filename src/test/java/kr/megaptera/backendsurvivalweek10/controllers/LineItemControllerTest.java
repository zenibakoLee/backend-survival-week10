package kr.megaptera.backendsurvivalweek10.controllers;

import kr.megaptera.backendsurvivalweek10.application.cart.AddProductToCartService;
import kr.megaptera.backendsurvivalweek10.application.cart.ChangeCartItemQuantityService;
import kr.megaptera.backendsurvivalweek10.application.cart.GetCartService;
import kr.megaptera.backendsurvivalweek10.dtos.CartDto;
import kr.megaptera.backendsurvivalweek10.models.LineItemId;
import kr.megaptera.backendsurvivalweek10.models.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LineItemController.class)
@ActiveProfiles("test")
class LineItemControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCartService getCartService;

    @MockBean
    private AddProductToCartService addProductToCartService;

    @MockBean
    private ChangeCartItemQuantityService changeCartItemQuantityService;

    @Test
    @DisplayName("GET /cart-line-items- success with login")
    void listWithLogin() throws Exception {
        given(getCartService.getCartDto(USER_ID)).willReturn(new CartDto(List.of()));

        mockMvc.perform(get("/cart-line-items")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("GET /cart-line-items- fail without login")
    void listWithoutLogin() throws Exception {
        given(getCartService.getCartDto(USER_ID)).willReturn(new CartDto(List.of()));

        mockMvc.perform(get("/cart-line-items"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /cart-line-items")
    void create() throws Exception {
        ProductId productId = new ProductId("test-id");

        String json = String.format(
                """
                        {
                            "productId": "%s",
                            "quantity": 3
                        }
                        """,
                productId
        );

        mockMvc.perform(post("/cart-line-items")
                .header("Authorization", "Bearer " + userAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isCreated());

        verify(addProductToCartService).addProduct(productId, 3, USER_ID);
    }

    @Test
    @DisplayName("PATCH /cart-line-items/{id} - with correct ID")
    void update() throws Exception {
        LineItemId lineItemId = new LineItemId("test-id");

        String json = "{\"quantity\": 3}";

        mockMvc.perform(patch("/cart-line-items/" + lineItemId)
                        .header("Authorization", "Bearer " + userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        verify(changeCartItemQuantityService).changeQuantity(lineItemId, 3, USER_ID);
    }

    @Test
    @DisplayName("PATCH /cart-line-items/{id} - with incorrect id token pair")
    void updateWithIncorrectID() throws Exception {
        LineItemId lineItemId = new LineItemId("BAD");

        String json = "{\"quantity\": 3}";

        mockMvc.perform(patch("/cart-line-items/" + lineItemId)
                        .header("Authorization", "Bearer " + "XXX")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
