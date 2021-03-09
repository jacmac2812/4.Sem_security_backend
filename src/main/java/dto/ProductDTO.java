/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Favorit;

/**
 *
 * @author jacobsimonsen
 */
public class ProductDTO {
    private String sku;
    private String name;
    private String type;
    private String regularPrice;
    private String salePrice;
    private String url;
    private String mobileUrl;
    private String image;
    private String shortDesription;
    private String onSale;

    public ProductDTO(String sku, String name, String type, String regularPrice, String salePrice, String url, String mobileUrl, String image, String shortDesription, String onSale) {
        this.sku = sku;
        this.name = name;
        this.type = type;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.url = url;
        this.mobileUrl = mobileUrl;
        this.image = image;
        this.shortDesription = shortDesription;
        this.onSale = onSale;
    }
    
    public ProductDTO(Favorit favorit) {
        this.sku = favorit.getSku();
        this.name = favorit.getName();
        this.type = favorit.getType();
        this.regularPrice = favorit.getRegularPrice();
        this.salePrice = favorit.getSalePrice();
        this.url = favorit.getUrl();
        this.image = favorit.getImage();
        this.onSale = favorit.getOnSale();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortDesription() {
        return shortDesription;
    }

    public void setShortDesription(String shortDesription) {
        this.shortDesription = shortDesription;
    }

    public String getOnSale() {
        return onSale;
    }

    public void setOnSale(String onSale) {
        this.onSale = onSale;
    }

}    