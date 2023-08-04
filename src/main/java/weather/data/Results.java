package weather.data;

public class Results {
    private String city;
    private String country;
    private String province;
    private String code;

    public Results(String city, String country, String province, String code) {
        this.city = city;
        this.country = country;
        this.province = province;
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCode() {
        return code;
    }

}
