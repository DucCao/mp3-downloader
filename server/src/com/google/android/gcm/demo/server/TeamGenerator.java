package com.google.android.gcm.demo.server;

import java.util.ArrayList;
import java.util.Collections;

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
	
	return "";
    }
    
    public static String generateTeamFoosBallInJson() {
	Collections.shuffle(playingMembers);
	
	return "";
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
