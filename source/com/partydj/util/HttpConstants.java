/***
 **
 ** This library is free software; you can redistribute it and/or
 ** modify it under the terms of the GNU Lesser General Public
 ** License as published by the Free Software Foundation; either
 ** version 2.1 of the License, or (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 ** Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public
 ** License along with this library; if not, write to the Free Software
 ** Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 **
 **/
package com.partydj.util;

public interface HttpConstants {

   /** 2XX: generally "OK" */
   public static final int HTTP_OK = 200;
   public static final int HTTP_CREATED = 201;
   public static final int HTTP_ACCEPTED = 202;
   public static final int HTTP_NOT_AUTHORITATIVE = 203;
   public static final int HTTP_NO_CONTENT = 204;
   public static final int HTTP_RESET = 205;
   public static final int HTTP_PARTIAL = 206;

   /** 3XX: relocation/redirect */
   public static final int HTTP_MULT_CHOICE = 300;
   public static final int HTTP_MOVED_PERM = 301;
   public static final int HTTP_MOVED_TEMP = 302;
   public static final int HTTP_SEE_OTHER = 303;
   public static final int HTTP_NOT_MODIFIED = 304;
   public static final int HTTP_USE_PROXY = 305;

   /** 4XX: client error */
   public static final int HTTP_BAD_REQUEST = 400;
   public static final int HTTP_UNAUTHORIZED = 401;
   public static final int HTTP_PAYMENT_REQUIRED = 402;
   public static final int HTTP_FORBIDDEN = 403;
   public static final int HTTP_NOT_FOUND = 404;
   public static final int HTTP_BAD_METHOD = 405;
   public static final int HTTP_NOT_ACCEPTABLE = 406;
   public static final int HTTP_PROXY_AUTH = 407;
   public static final int HTTP_CLIENT_TIMEOUT = 408;
   public static final int HTTP_CONFLICT = 409;
   public static final int HTTP_GONE = 410;
   public static final int HTTP_LENGTH_REQUIRED = 411;
   public static final int HTTP_PRECON_FAILED = 412;
   public static final int HTTP_ENTITY_TOO_LARGE = 413;
   public static final int HTTP_REQ_TOO_LONG = 414;
   public static final int HTTP_UNSUPPORTED_TYPE = 415;

   /** 5XX: server error */
   public static final int HTTP_SERVER_ERROR = 500;
   public static final int HTTP_INTERNAL_ERROR = 501;
   public static final int HTTP_BAD_GATEWAY = 502;
   public static final int HTTP_UNAVAILABLE = 503;
   public static final int HTTP_GATEWAY_TIMEOUT = 504;
   public static final int HTTP_VERSION = 505;
   
   //Methods
   public static final String POST = "POST";
   public static final String GET  = "GET";
   
   //Headers
   public static final String CONTENT_TYPE = "Content-type";
   public static final String CONTENT_DISPOSITION = "Content-Disposition";
   public static final String HOST = "Host";
   
   //other
   public static final String MULTIPART = "multipart/form-data";
   public static final String BOUNDARY = "boundary";
   
}
