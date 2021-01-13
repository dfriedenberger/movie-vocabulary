package de.frittenburger.movievocabulary.youtube.impl;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import de.frittenburger.movievocabulary.youtube.model.YoutubeVideoInfo;

public class JsonToYoutubeInfoMapper implements Function<JsonNode, YoutubeVideoInfo> {

	@Override
	public YoutubeVideoInfo apply(JsonNode t) {
	
		String kind = t.get("kind").asText();
		if(kind.equals("youtube#videoListResponse"))
		{
			JsonNode item = t.get("items").get(0);
			String itemKind = item.get("kind").asText();
			if(itemKind.equals("youtube#video"))
			{
				//{"publishedAt":"2017-11-20T19:46:53Z","channelId":"UCsT0YIqwnpJCM-mx7-gSA4Q",
				//"title":"Zombies en la escuela | Juli Garbulsky | TEDxRiodelaPlata",
				//"description":"¿Cómo se sienten los adolescentes cuando están en la escuela? Juli Garbulsky acaba de terminar la secundaria y en esta charla comparte una visión que puede ayudarnos a pensar distinto en el futuro de la educación. Julián terminó la secundaria el año pasado y está estudiando la licenciatura en Matemática. Tiene 19 años pero la mamá lo sigue retando si sale sin abrigo. Le encanta la física del vuelo de los aviones y armar modelos a control remoto, aunque siempre se caigan. Disfruta la música pero canta mal. También le divierte mucho pensar acertijos de lógica. Como no le gusta ver series, lo único que no le podés spoilear es la respuesta de un acertijo. This talk was given at a TEDx event using the TED conference format but independently organized by a local community. Learn more at https://www.ted.com/tedx",
				//"thumbnails":{"default":{"url":"https://i.ytimg.com/vi/g6zBmBUOMhY/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/g6zBmBUOMhY/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/g6zBmBUOMhY/hqdefault.jpg","width":480,"height":360}},
				//"channelTitle":"TEDx Talks","tags":["TEDxTalks","Spanish","Education","Childhood","Curiosity","Motivation","Schools","Teaching"],

				JsonNode snippet = item.get("snippet");
				JsonNode thumbnails = snippet.get("thumbnails");
				
				YoutubeVideoInfo info = new YoutubeVideoInfo();
				info.setTitle(snippet.get("title").asText());
				info.setPoster(thumbnails.get("high").get("url").asText());
				info.setLanguage(snippet.get("defaultAudioLanguage").asText());

				return info;
			}
		}
		
		return null;
	}

}
