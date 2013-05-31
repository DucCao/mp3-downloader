package com.google.android.gcm.demo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Mp3Parser {

    public static List<String> parseNctPlaylist(String url) throws Exception {
        Document doc = Jsoup.parse(new URL(url), 30000);
        Elements elementPlayList = doc.select(".list-song-plist [key]");
        
        List<String> mp3Links = new ArrayList<String>();
        String nctLink = null;
        String mp3Link = null;
        for (int i = 0; i < elementPlayList.size(); ++i) {
            nctLink = "http://www.nhaccuatui.com/download/song/" + elementPlayList.get(i).attr("key");
            
            org.json.JSONObject json = new org.json.JSONObject(get(nctLink));
            if (json.getInt("error_code") == 0) {
                mp3Link = json.getJSONObject("data").getString("stream_url");
                mp3Links.add(mp3Link);
            }
        }
        
        return mp3Links;
    }
    
    private static String get(String link) {
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        
        return builder.toString();
    }
}
