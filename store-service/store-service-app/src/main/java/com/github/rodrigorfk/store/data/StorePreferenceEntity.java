package com.github.rodrigorfk.store.data;

import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
public class StorePreferenceEntity {

    @Id
    private Long storeId;
    private Integer maxItensInCart;

    public StorePreferenceData toData(){
        return StorePreferenceData.builder()
                .storeId(storeId)
                .maxItensInCart(maxItensInCart)
                .build();
    }
}
