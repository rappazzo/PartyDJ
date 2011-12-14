/***
 **  @(#) TradeCard.com 1.0
 **
 **  Copyright (c) 2011 TradeCard, Inc. All Rights Reserved.
 **
 **
 **  THIS COMPUTER SOFTWARE IS THE PROPERTY OF TradeCard, Inc.
 **
 **  Permission is granted to use this software as specified by the TradeCard
 **  COMMERCIAL LICENSE AGREEMENT.  You may use this software only for
 **  commercial purposes, as specified in the details of the license.
 **  TRADECARD SHALL NOT BE LIABLE FOR ANY  DAMAGES SUFFERED BY
 **  THE LICENSEE AS A RESULT OF USING OR MODIFYING THIS SOFTWARE IN ANY WAY.
 **
 **  YOU MAY NOT DISTRIBUTE ANY SOURCE CODE OR OBJECT CODE FROM THE TradeCard.com
 **  TOOLKIT AT ANY TIME. VIOLATORS WILL BE PROSECUTED TO THE FULLEST EXTENT
 **  OF UNITED STATES LAW.
 **
 **  @version 1.0
 **  @author Copyright (c) 2011 TradeCard, Inc. All Rights Reserved.
 **
 **/
package com.partydj.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.xbill.DNS.*;

/**
 * Mostly adapted from jnamed.java (distributed with org.javadns)
 **/
public class DNS {

   static final int FLAG_DNSSECOK = 1;
   static final int FLAG_SIGONLY = 2;
   
   ExecutorService DNS_WORKER = Executors.newCachedThreadPool(NamedThreadFactory.createDaemonFactory("DNS Worker #"));

   Zone zone;
   Map<Integer, Cache> caches = new HashMap();

   public static DNS create(String zoneName, String ip) {
      return new DNS(zoneName, ip);
   }

   /*********************************************************************
    * Create and run a new DNS server on port 53.
    *********************************************************************
    * @param zonefile A master file for the zone to serve.
    */
   private DNS(String zoneName, String ip) {
      // Load our master file.
      try {
         Name zname = Name.fromString(zoneName, Name.root);
         zone = new Zone(zname, DClass.IN, ip);
      } catch (Exception e) {
         System.out.println("Error setting up DNS");
         e.printStackTrace();
         return;
      }

      // Start our two server processes.
      addUDP(53);
      addTCP(53);
   }

   public Cache getCache(int dclass) {
      Cache c = caches.get(new Integer(dclass));
      if (c == null) {
         c = new Cache(dclass);
         caches.put(new Integer(dclass), c);
      }
      return c;
   }

   // Create a new TCP listener process.
   private void addTCP(final int port) {
      Executors.newSingleThreadExecutor().submit(new Runnable() {
         public void run() {
            serveTCP(port);
         }
      });
   }

   // Create a new UDP listener process.
   private void addUDP(final int port) {
      Thread t;
      Executors.newSingleThreadExecutor().submit(new Runnable() {
         public void run() {
            serveUDP(port);
         }
      });
   }

   // Our TCP listener process.
   private void serveTCP(int port) {
      try {
         ServerSocket sock = new ServerSocket(port);
         while (true) {
            final Socket s = sock.accept();
            DNS_WORKER.submit(new Runnable() {
               public void run() {
                  processTCP(s);
               }
            });
         }
      } catch (IOException e) {
         System.out.println("serveTCP failed: " + e);
      }
   }

   // Read and respond to a TCP request.
   private void processTCP(Socket s) {
      try {
         InputStream is = s.getInputStream();
         DataInputStream dataIn = new DataInputStream(is);
         int inLength = dataIn.readUnsignedShort();
         byte[] in = new byte[inLength];
         dataIn.readFully(in);

         byte[] response = null;
         try {
            Message  query = new Message(in);
            response = generateReply(query, in, in.length, s);
            if (response == null) return;
         } catch (IOException e) {
            response = formerrMessage(in);
         }
         DataOutputStream dataOut = new DataOutputStream(s.getOutputStream());
         dataOut.writeShort(response.length);
         dataOut.write(response);
      } catch (IOException e) {
         String addrString;
         System.out.println("TCPclient(" + s.getLocalAddress().getHostAddress() + "#" + s.getLocalPort() + "): " + e);
      } finally {
         try {
            s.close();
         } catch (IOException e) {
         }
      }
   }

   // Our UDP listener process.
   private void serveUDP(int port) {
      try {
         DatagramSocket sock = new DatagramSocket(port);
         final short udpLength = 512;
         byte[] in = new byte[udpLength];
         DatagramPacket indp = new DatagramPacket(in, in.length);
         DatagramPacket outdp = null;
         while (true) {
            indp.setLength(in.length);
            try {
               sock.receive(indp);
            } catch (InterruptedIOException e) {
               continue;
            }
            Message query;
            byte[] response = null;
            try {
               query = new Message(in);
               response = generateReply(query, in, indp.getLength(), null);
               if (response == null) continue;
            } catch (IOException e) {
               response = formerrMessage(in);
            }
            if (outdp == null) {
               outdp = new DatagramPacket(response, response.length, indp.getAddress(), indp.getPort());
            } else {
               outdp.setData(response);
               outdp.setLength(response.length);
               outdp.setAddress(indp.getAddress());
               outdp.setPort(indp.getPort());
            }
            sock.send(outdp);
         }
      } catch (IOException e) {
         System.out.println("serveUDP(0.0.0.0#" + port + "): " + e);
      }
   }

   public Zone findBestZone(Name name) {
      if (zone.getOrigin().equals(name)) {
         return zone;
      }
      return null;
   }

   public RRset findExactMatch(Name name, int type, int dclass, boolean glue) {
      Zone zone = findBestZone(name);
      if (zone != null) {
         return zone.findExactMatch(name, type);
      } else {
         RRset[] rrsets;
         Cache cache = getCache(dclass);
         if (glue)
            rrsets = cache.findAnyRecords(name, type);
         else
            rrsets = cache.findRecords(name, type);
         if (rrsets == null)
            return null;
         else
            return rrsets[0]; /* not quite right */
      }
   }

   void addRRset(Name name, Message response, RRset rrset, int section, int flags) {
      for (int s = 1; s <= section; s++)
         if (response.findRRset(name, rrset.getType(), s)) return;
      if ((flags & FLAG_SIGONLY) == 0) {
         Iterator it = rrset.rrs();
         while (it.hasNext()) {
            Record r = (Record)it.next();
            if (r.getName().isWild() && !name.isWild()) r = r.withName(name);
            response.addRecord(r, section);
         }
      }
      if ((flags & (FLAG_SIGONLY | FLAG_DNSSECOK)) != 0) {
         Iterator it = rrset.sigs();
         while (it.hasNext()) {
            Record r = (Record)it.next();
            if (r.getName().isWild() && !name.isWild()) r = r.withName(name);
            response.addRecord(r, section);
         }
      }
   }

   private final void addSOA(Message response, Zone zone) {
      response.addRecord(zone.getSOA(), Section.AUTHORITY);
   }

   private final void addNS(Message response, Zone zone, int flags) {
      RRset nsRecords = zone.getNS();
      addRRset(nsRecords.getName(), response, nsRecords, Section.AUTHORITY, flags);
   }

   private final void addCacheNS(Message response, Cache cache, Name name) {
      SetResponse sr = cache.lookupRecords(name, Type.NS, Credibility.HINT);
      if (!sr.isDelegation()) return;
      RRset nsRecords = sr.getNS();
      Iterator it = nsRecords.rrs();
      while (it.hasNext()) {
         Record r = (Record)it.next();
         response.addRecord(r, Section.AUTHORITY);
      }
   }

   private void addGlue(Message response, Name name, int flags) {
      RRset a = findExactMatch(name, Type.A, DClass.IN, true);
      if (a == null) return;
      addRRset(name, response, a, Section.ADDITIONAL, flags);
   }

   private void addAdditional2(Message response, int section, int flags) {
      Record[] records = response.getSectionArray(section);
      for (int i = 0; i < records.length; i++) {
         Record r = records[i];
         Name glueName = r.getAdditionalName();
         if (glueName != null) addGlue(response, glueName, flags);
      }
   }

   private final void addAdditional(Message response, int flags) {
      addAdditional2(response, Section.ANSWER, flags);
      addAdditional2(response, Section.AUTHORITY, flags);
   }

   byte addAnswer(Message response, Name name, int type, int dclass, int iterations, int flags) {
      SetResponse sr;
      byte rcode = Rcode.NOERROR;

      if (iterations > 6) return Rcode.NOERROR;

      if (type == Type.SIG || type == Type.RRSIG) {
         type = Type.ANY;
         flags |= FLAG_SIGONLY;
      }

      Zone zone = findBestZone(name);
      if (zone != null)
         sr = zone.findRecords(name, type);
      else {
         Cache cache = getCache(dclass);
         sr = cache.lookupRecords(name, type, Credibility.NORMAL);
      }

      if (sr.isUnknown()) {
         addCacheNS(response, getCache(dclass), name);
      }
      if (sr.isNXDOMAIN()) {
         response.getHeader().setRcode(Rcode.NXDOMAIN);
         if (zone != null) {
            addSOA(response, zone);
            if (iterations == 0) response.getHeader().setFlag(Flags.AA);
         }
         rcode = Rcode.NXDOMAIN;
      } else if (sr.isNXRRSET()) {
         if (zone != null) {
            addSOA(response, zone);
            if (iterations == 0) response.getHeader().setFlag(Flags.AA);
         }
      } else if (sr.isDelegation()) {
         RRset nsRecords = sr.getNS();
         addRRset(nsRecords.getName(), response, nsRecords, Section.AUTHORITY, flags);
      } else if (sr.isCNAME()) {
         CNAMERecord cname = sr.getCNAME();
         RRset rrset = new RRset(cname);
         addRRset(name, response, rrset, Section.ANSWER, flags);
         if (zone != null && iterations == 0) response.getHeader().setFlag(Flags.AA);
         rcode = addAnswer(response, cname.getTarget(), type, dclass, iterations + 1, flags);
      } else if (sr.isDNAME()) {
         DNAMERecord dname = sr.getDNAME();
         RRset rrset = new RRset(dname);
         addRRset(name, response, rrset, Section.ANSWER, flags);
         Name newname;
         try {
            newname = name.fromDNAME(dname);
         } catch (NameTooLongException e) {
            return Rcode.YXDOMAIN;
         }
         rrset = new RRset(new CNAMERecord(name, dclass, 0, newname));
         addRRset(name, response, rrset, Section.ANSWER, flags);
         if (zone != null && iterations == 0) response.getHeader().setFlag(Flags.AA);
         rcode = addAnswer(response, newname, type, dclass, iterations + 1, flags);
      } else if (sr.isSuccessful()) {
         RRset[] rrsets = sr.answers();
         for (int i = 0; i < rrsets.length; i++)
            addRRset(name, response, rrsets[i], Section.ANSWER, flags);
         if (zone != null) {
            addNS(response, zone, flags);
            if (iterations == 0) response.getHeader().setFlag(Flags.AA);
         } else
            addCacheNS(response, getCache(dclass), name);
      }
      return rcode;
   }

   byte[] doAXFR(Name name, Message query, TSIG tsig, TSIGRecord qtsig, Socket s) {
      Zone zone = findBestZone(name);
      boolean first = true;
      if (zone == null) return errorMessage(query, Rcode.REFUSED);
      Iterator it = zone.AXFR();
      try {
         DataOutputStream dataOut;
         dataOut = new DataOutputStream(s.getOutputStream());
         int id = query.getHeader().getID();
         while (it.hasNext()) {
            RRset rrset = (RRset)it.next();
            Message response = new Message(id);
            Header header = response.getHeader();
            header.setFlag(Flags.QR);
            header.setFlag(Flags.AA);
            addRRset(rrset.getName(), response, rrset, Section.ANSWER, FLAG_DNSSECOK);
            if (tsig != null) {
               tsig.applyStream(response, qtsig, first);
               qtsig = response.getTSIG();
            }
            first = false;
            byte[] out = response.toWire();
            dataOut.writeShort(out.length);
            dataOut.write(out);
         }
      } catch (IOException ex) {
         System.out.println("AXFR failed");
      }
      try {
         s.close();
      } catch (IOException ex) {
      }
      return null;
   }

   /*
    * Note: a null return value means that the caller doesn't need to do
    * anything.  Currently this only happens if this is an AXFR request over
    * TCP.
    */
   byte[] generateReply(Message query, byte[] in, int length, Socket s) throws IOException {
      Header header;
      boolean badversion;
      int maxLength;
      boolean sigonly;
      SetResponse sr;
      int flags = 0;

      header = query.getHeader();
      if (header.getFlag(Flags.QR)) return null;
      if (header.getRcode() != Rcode.NOERROR) return errorMessage(query, Rcode.FORMERR);
      if (header.getOpcode() != Opcode.QUERY) return errorMessage(query, Rcode.NOTIMP);

      Record queryRecord = query.getQuestion();

      TSIGRecord queryTSIG = query.getTSIG();
      TSIG tsig = null;
      if (queryTSIG != null) {
         return formerrMessage(in);
      }

      OPTRecord queryOPT = query.getOPT();
      if (queryOPT != null && queryOPT.getVersion() > 0) badversion = true;

      if (s != null)
         maxLength = 65535;
      else if (queryOPT != null)
         maxLength = Math.max(queryOPT.getPayloadSize(), 512);
      else
         maxLength = 512;

      if (queryOPT != null && (queryOPT.getFlags() & ExtendedFlags.DO) != 0) flags = FLAG_DNSSECOK;

      Message response = new Message(query.getHeader().getID());
      response.getHeader().setFlag(Flags.QR);
      if (query.getHeader().getFlag(Flags.RD)) response.getHeader().setFlag(Flags.RD);
      response.addRecord(queryRecord, Section.QUESTION);

      Name name = queryRecord.getName();
      int type = queryRecord.getType();
      int dclass = queryRecord.getDClass();
      if (type == Type.AXFR && s != null) return doAXFR(name, query, tsig, queryTSIG, s);
      if (!Type.isRR(type) && type != Type.ANY) return errorMessage(query, Rcode.NOTIMP);

      byte rcode = addAnswer(response, name, type, dclass, 0, flags);
      if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN) return errorMessage(query, rcode);

      addAdditional(response, flags);

      if (queryOPT != null) {
         int optflags = (flags == FLAG_DNSSECOK) ? ExtendedFlags.DO : 0;
         OPTRecord opt = new OPTRecord((short)4096, rcode, (byte)0, optflags);
         response.addRecord(opt, Section.ADDITIONAL);
      }

      response.setTSIG(tsig, Rcode.NOERROR, queryTSIG);
      return response.toWire(maxLength);
   }

   byte[] buildErrorMessage(Header header, int rcode, Record question) {
      Message response = new Message();
      response.setHeader(header);
      for (int i = 0; i < 4; i++)
         response.removeAllRecords(i);
      if (rcode == Rcode.SERVFAIL) response.addRecord(question, Section.QUESTION);
      header.setRcode(rcode);
      return response.toWire();
   }

   public byte[] formerrMessage(byte[] in) {
      Header header;
      try {
         header = new Header(in);
      } catch (IOException e) {
         return null;
      }
      return buildErrorMessage(header, Rcode.FORMERR, null);
   }

   public byte[] errorMessage(Message query, int rcode) {
      return buildErrorMessage(query.getHeader(), rcode, query.getQuestion());
   }

}
