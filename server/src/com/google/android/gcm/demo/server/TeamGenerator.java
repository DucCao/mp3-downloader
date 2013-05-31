package com.google.android.gcm.demo.server;

import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TeamGenerator {
    
    private static final String[] MEMBERS = {
	"Duc",
	"Huy",
	"Khang",
	"Gia",
	"Bach",
	"Quan",
	"Nhan",
	"Phu",
	"Triet",
	"Anh",
	"CuongThai",
	"CuongLy"
    };
    
    private static ArrayList<String> playingMembers = new ArrayList<String>();
    
    static {
	for (String member : MEMBERS) {
	    playingMembers.add(member);
	}
    }

    public static String generateCSTeamsInJson() {
	ArrayList<String> list = new ArrayList<String>();
	for (String str : MEMBERS) {
	    list.add(str);
	}
	Collections.shuffle(list);
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<String> list2 = new ArrayList<String>();
	for (int i = 0; i < list.size(); ++i) {
	    if (i < list.size() / 2) {
		list1.add(list.get(i));
	    } else {
		list2.add(list.get(i));
	    }
	}
	
	JSONObject jsonObject = new JSONObject();
	jsonObject.put("team1", list1);
	jsonObject.put("team2", list2);
	
	return jsonObject.toJSONString();
    }
    
    public static String generateTeamFoosBallInJson() {
	Collections.shuffle(playingMembers);
	
	JSONArray array = new JSONArray();
	for (int i = 0; i < playingMembers.size(); i += 2) {
	    JSONArray team = new JSONArray();
	    if (i < playingMembers.size()) {
		team.add(playingMembers.get(i));
	    }
	    if (i + 1 < playingMembers.size()) {
		team.add(playingMembers.get(i + 1));
	    }
	    
	    array.add(team);
	}
	
	return array.toJSONString();
    }
    
    public static void updateMembers(String removedMembers) {
	playingMembers.clear();
	for (String str : MEMBERS) {
	    playingMembers.add(str);
	}
	
	if (removedMembers != null && !removedMembers.equals("")) {
	    String[] members = removedMembers.split(",");
	    for (String member : members) {
		playingMembers.remove(member);
	    }
	}
    }
}
