public class Search {
    private String city;
    private String country;
    private String province;
    private String code;

    public Search(String city, String country, String province, String code) {
        this.city = city;
        this.country = country;
        this.province = province;
        this.code = code;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public  void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}
