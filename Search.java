public class Search {
    private String city;
    private String country;
    private String province;

    public Search() {
    }

    public Search(String city, String country, String province) {
        this.city = city;
        this.country = country;
        this.province = province;
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

}
