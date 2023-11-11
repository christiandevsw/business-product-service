package com.dojo.food.services.business.menu.product.business.other;

public interface ConvertService<E,D> {
    E convertToEntity(D t);
    D convertToDto(E t);
}
