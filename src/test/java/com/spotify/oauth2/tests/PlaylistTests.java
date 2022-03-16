package com.spotify.oauth2.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.spotify.oauth2.pojo.Playlist;
import com.spotify.oauth2.utils.DataLoader;
import com.spotify.oauth2.utils.FakerUtils;
import com.spotify.oauth2.api.SpecBuilder;
import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.api.applicationApi.PlayListApi;
import com.spotify.oauth2.pojo.Error;

import io.qameta.allure.Description;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class PlaylistTests {
	
	
@Description("This is the first case description")	
@Test(description="should be able to create a playlist")
public void shouldBeAbleToCreatePlayList()
{
	Playlist requestPlayList=playListBuilder(FakerUtils.generateName(),FakerUtils.generateDescription(),false);
	
	Response response = PlayListApi.post(requestPlayList);
	assertStatusCode(response.statusCode(),StatusCode.CODE_201.getCode());
	assertPlaylistEqual(response.as(Playlist.class),requestPlayList);	
}
@Test(description="should be able to get a playlist")
public void shouldBeAbleToGetPlayList()
{
	Playlist requestPlayList=playListBuilder("Updated Playlist Name","Updated playlist description",false);
	
	Response response=PlayListApi.get(DataLoader.getInstance().getPlayListId());
	assertStatusCode(response.statusCode(),StatusCode.CODE_200.getCode());
	assertPlaylistEqual(response.as(Playlist.class),requestPlayList);	
	
}

@Test(description="should be able to update a playlist")
public void shouldBeAbleToUpdateAPlayList()
{
	Playlist requestPlayList=playListBuilder(FakerUtils.generateName(),FakerUtils.generateDescription(),false);
	Response response=PlayListApi.update(DataLoader.getInstance().getUpdatePlayListId(), requestPlayList);
	assertStatusCode(response.statusCode(),StatusCode.CODE_200.getCode());
}

@Test(description="should not be able to create a playlist without name")
public void shouldNotBeAbleToCreatePlayListWithoutName()
{
	Playlist requestPlayList=playListBuilder("",FakerUtils.generateDescription(),false);
	Response response=PlayListApi.post(requestPlayList);
	assertStatusCode(response.statusCode(),StatusCode.CODE_400.getCode());
	assertError(response.as(Error.class),StatusCode.CODE_400.getCode(),StatusCode.CODE_400.getMsg());
	
}
@Test(description="should not be able to create a playlist without token")
public void shouldNotBeAbleToCreatePlayListWithIncorrectToken()
{  
	Playlist requestPlayList=playListBuilder(FakerUtils.generateName(),FakerUtils.generateDescription(),false);	
	Response response=PlayListApi.post("12345",requestPlayList);
	assertStatusCode(response.statusCode(),StatusCode.CODE_401.getCode());
	assertError(response.as(Error.class),StatusCode.CODE_401.getCode(),StatusCode.CODE_401.getMsg());
	
}
public Playlist playListBuilder(String name, String description,boolean _public)
{
	return  Playlist.builder().
	name(name).
	description(description).
	_public(_public).build();
	
	
}

public void assertPlaylistEqual(Playlist responsePlayList,Playlist requestPlayList) {
	
	assertThat(responsePlayList.getName(), equalTo(requestPlayList.getName()));
	assertThat(responsePlayList.getDescription(), equalTo(requestPlayList.getDescription()));
	assertThat(responsePlayList.get_public(), equalTo(requestPlayList.get_public()));
}
public void assertStatusCode(int actualStatusCode,int expectedStatusCode)
{
	assertThat(actualStatusCode,equalTo(expectedStatusCode));
}

public void assertError(Error responseError,int expectedStatusCode,String errorMsg) {
	
	assertThat(responseError.getError().getStatus(),equalTo(expectedStatusCode));
	assertThat(responseError.getError().getMessage(),equalTo(errorMsg));
}
}
