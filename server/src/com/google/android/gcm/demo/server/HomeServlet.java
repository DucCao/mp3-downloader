/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

/**
 * Servlet that adds display number of devices and button to send a message.
 * <p>
 * This servlet is used just by the browser (i.e., not device) and contains the
 * main page of the demo app.
 */
@SuppressWarnings("serial")
public class HomeServlet extends BaseServlet {

  static final String ATTRIBUTE_STATUS = "status";

  /**
   * Displays the existing messages and offer the option to send a new one.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
      
      resp.setContentType("application/json");
      try {
          String nctLink = "http://www.nhaccuatui.com/playlist/tuyen-tap-nhac-quoc-te-bat-hu-va.tcfuADGqVpy0.html";
          List<String> listMp3Files = Mp3Parser.parseNctPlaylist(nctLink);
          resp.getWriter().println(new JSONArray(listMp3Files).toString());
      } catch (Exception e) {
          e.printStackTrace();
      }

//      try {
//          getServletContext().getRequestDispatcher("/sendAll").forward(req, resp);
//      } catch (ServletException e) {
//          e.printStackTrace();
//      }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    doGet(req, resp);
  }

}
