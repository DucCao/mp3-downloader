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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

/**
 * Servlet that sends a message to a device.
 * <p>
 * This servlet is invoked by AppEngine's Push Queue mechanism.
 */
@SuppressWarnings("serial")
public class GetMp3LinksServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String nctLink = req.getParameter("nctLink");
        try {
            List<String> listMp3Files = Mp3Parser.parseNctPlaylist(nctLink);
            
            resp.setContentType("application/json");
            resp.getWriter().print(new JSONArray(listMp3Files));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
