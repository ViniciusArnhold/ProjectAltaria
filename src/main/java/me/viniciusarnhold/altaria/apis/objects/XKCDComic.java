package me.viniciusarnhold.altaria.apis.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
@SuppressWarnings("unused") //Methods are used by Jackson ObjectMapper
public class XKCDComic {

    @JsonProperty("month")
    private String month;
    @JsonProperty("num")
    private int comicNumber;
    @JsonProperty("link")
    private String link;
    @JsonProperty("year")
    private String year;
    @JsonProperty("news")
    private String news;
    @JsonProperty("safe_title")
    private String safeTitle;
    @JsonProperty("transcript")
    private String transcript;
    @JsonProperty("alt")
    private String altText;
    @JsonProperty("img")
    private String imageLink;
    @JsonProperty("title")
    private String title;
    @JsonProperty("day")
    private String day;

    public XKCDComic() {
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getComicNumber() {
        return comicNumber;
    }

    public void setComicNumber(int comicNumber) {
        this.comicNumber = comicNumber;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getSafeTitle() {
        return safeTitle;
    }

    public void setSafeTitle(String safeTitle) {
        this.safeTitle = safeTitle;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
