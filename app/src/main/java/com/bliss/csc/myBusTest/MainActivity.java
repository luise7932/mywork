package com.bliss.csc.myBusTest;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView busNumer;
    EditText editPhoneNum;
    Button search;
    Button select;
    String getData;
    int busNumber = 0;
    int busNum = 503;
    String strSrch = busNumber + "";
    double gpsX, gpsY;
    int count;
    String Input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.data);
        busNumer = (TextView) findViewById(R.id.bus);
        editPhoneNum = (EditText) findViewById(R.id.editPhoneNum);
        search = (Button) findViewById(R.id.search);
        select = (Button) findViewById(R.id.select);
        //     String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";

        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";

        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;

        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);

    }

    public class DownloadWebContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadByUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            //xml 문서를 파싱하는 방법으로 본 예제에서는  Pull Parer 를 사용한다.
            String headerCd = "";
            String busRouteId = "";

            boolean bus_headerCd = false;
            boolean bus_busRouteId = false;

            // textView.append("===== 노선ID =====\n");
            try {

                //XmlPullParser 를 사용하기 위해서 XmlPullParserFactory 객체를 생성함
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlpp = factory.newPullParser();

                //parser 에 url를 입력함
                xmlpp.setInput(new StringReader(result));


                //parser 이벤트를 저장할 변수 지정
                int eventType = xmlpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {   //문서가 마지막이 아니면
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xmlpp.getName();
                        if (tag_name.equals("headerCd"))
                            bus_headerCd = true;
                        if (tag_name.equals("busRouteId"))
                            bus_busRouteId = true;

                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bus_headerCd) {
                            headerCd = xmlpp.getText();
                            bus_headerCd = false;
                        }

                        if (headerCd.equals("0")) {
                            if (bus_busRouteId) {
                                busRouteId = xmlpp.getText();
                                bus_busRouteId = false;
                            }

                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xmlpp.next();
                }
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }

            String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute";
            String serviceUrl2 = "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRtid";

            String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";

            String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&busRouteId=" + busRouteId;
            String strUrl2 = serviceUrl2 + "?ServiceKey=" + serviceKey + "&busRouteId=" + busRouteId;

            //DownloadWebContent2 dwc2 = new DownloadWebContent2();
            DownloadWebContent3 dwc3 = new DownloadWebContent3();
            //dwc2.execute(strUrl);
            dwc3.execute(strUrl2);

        }

        public String downloadByUrl(String myurl) throws IOException {
            //Http 통신: HttpURLConnection 클래스를 활용해 데이터를 얻는다.

            HttpURLConnection conn = null;
            try {
                //요청 URL, 전달받은 url string 으로 URL 객체를 만듦
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();

                BufferedInputStream buffer = new BufferedInputStream(conn.getInputStream());

                BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(buffer, "utf-8"));

                String line = null;
                getData = "";
                while ((line = buffer_reader.readLine()) != null) {
                    getData += line;

                }
                return getData;
            } finally {
                //접속 해제
                conn.disconnect();
            }
        }

    }
/*
    public class DownloadWebContent2 extends AsyncTask <String, Void, String> {


        @Override

        protected String doInBackground(String... urls) {
            try {
                return (String) downloadByUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            String headerCd = "";
            String gpsX = "";
            String gpsY = "";
            String stationNm = "";
            String direction = "";
            String sectSpd = "";
            String station = "";
            String stationNo = "";

            boolean bus_headerCd = false;
            boolean bus_gpsX = false;
            boolean bus_gpsY = false;
            boolean bus_stationNm = false;
            boolean bus_sectSpd = false;
            boolean bus_direction = false;
            boolean bus_station = false;
            boolean bus_stationNo = false;

            ///// (2) Bus Positions
            textView.append("-버스 위치 검색 결과-\n");
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlpp = factory.newPullParser();

                xmlpp.setInput(new StringReader(result));
                int eventType = xmlpp.getEventType();

                count = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xmlpp.getName();

                        switch (tag_name) {

                            case "headerCd":
                                bus_headerCd = true;
                                break;
                            case "gpsX":
                                bus_gpsX = true;
                                break;
                            case "gpsY":
                                bus_gpsY = true;
                                break;
                            case "sectSpd":
                                bus_sectSpd = true;
                                break;
                            case "stationNm":
                                bus_stationNm = true;
                                break;
                            case "direction":
                                bus_direction = true;
                                break;
                            case "station":
                                bus_station = true;
                            case "stationNo":
                                bus_stationNo = true;


                        }

                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bus_headerCd) {
                            headerCd = xmlpp.getText();
                            // textView.append("headerCd: " + headerCd + "\n");
                            bus_headerCd = false;
                        }

                        if (headerCd.equals("0")) {
                            if (bus_gpsX) {
                                count++;
                                textView.append("-------------------------------------------\n");

                                gpsX = xmlpp.getText();
                                textView.append("(" + count + ") gpsX: " + gpsX + "\n");
                                bus_gpsX = false;
                            }
                            if (bus_gpsY) {
                                gpsY = xmlpp.getText();
                                textView.append("(" + count + ") gpsY: " + gpsY + "\n");
                                bus_gpsY = false;
                            }

                            if (bus_stationNm) {
                                stationNm = xmlpp.getText();
                                textView.append("(" + count + ") 정류장이름: " + stationNm + "\n");
                                bus_stationNm = false;
                            }

                            if (bus_direction) {
                                direction = xmlpp.getText();
                                textView.append("(" + count + ") 진행방향: " + direction + "\n");
                                bus_direction = false;
                            }

                            if (bus_sectSpd) {
                                sectSpd = xmlpp.getText();
                                textView.append("(" + count + ") 구간속도: " + sectSpd + "\n");
                                bus_sectSpd = false;
                            }

                            if (bus_station) {
                                station = xmlpp.getText();
                                textView.append("(" + count + ") 정류장id: " + station + "\n");
                                bus_station = false;
                            }
                            if (bus_stationNo) {
                                stationNo = xmlpp.getText();
                                textView.append("(" + count + ") 정류장 고유: " + stationNo + "\n");
                                bus_stationNo = false;
                            }

                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xmlpp.next();
                }
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }

        }


        public String downloadByUrl(String myurl) throws IOException {
            //Java와 Http 통신: HttpURLConnection 클래스를 활용해 데이터를 얻는다.

            HttpURLConnection conn = null;
           // HttpURLConnection conn2 = null;
            BufferedReader buffer_reader;
           // BufferedReader buffer_reader2;
            try {
                //요청 URL, 전달받은 url string 으로 URL 객체를 만듦
                URL url = new URL(myurl);
                //URL url2 = new URL(myurl2);
                conn = (HttpURLConnection) url.openConnection();
                //conn2 = (HttpURLConnection) url2.openConnection();
                conn.setRequestMethod("GET");
              //conn2.setRequestMethod("GET");
                //결과를 InputStream으로 받아서 INputStreamReader, BufferedReader로 캐스팅한다.
                BufferedInputStream buffer = new BufferedInputStream(conn.getInputStream());
                //BufferedInputStream buffer2 = new BufferedInputStream(conn2.getInputStream());
                buffer_reader = new BufferedReader(new InputStreamReader(buffer, "utf-8"));
               //buffer_reader2 = new BufferedReader(new InputStreamReader(buffer2, "utf-8"));
                String line = null;
                String line2 = null;
                getData = "";
                while ((line = buffer_reader.readLine()) != null) {
                    getData += line;
                }

                return getData;
            } finally {
                //접속 해제
                conn.disconnect();
            }

        }
    }*/


    public class DownloadWebContent3 extends AsyncTask <String, Void, String> {


        @Override

        protected String doInBackground(String... urls) {
            try {
                return (String) downloadByUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {

            String headerCd = "";
            String gpsX = "";
            String gpsY = "";
            String busType = ""; // 도착하는 버스의 일반버스 저상버스 타입 코드
            String nextStId;
            String plainNo;

            boolean bus_headerCd = false;
            boolean bus_gpsX = false;
            boolean bus_gpsY = false;
            boolean bus_busType = false;
            boolean bus_nextStId = false;
            boolean bus_plainNo = false;

            ///// (2) Bus Positions
            textView.append("-버스 위치 검색 결과-\n");
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlpp = factory.newPullParser();

                xmlpp.setInput(new StringReader(result));
                int eventType = xmlpp.getEventType();

                count = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xmlpp.getName();

                        switch (tag_name) {

                            case "headerCd":
                                bus_headerCd = true;
                                break;
                            case "gpsX":
                                bus_gpsX = true;
                                break;
                            case "gpsY":
                                bus_gpsY = true;
                                break;
                            case "busType":
                                bus_busType = true;
                                break;
                            case "nextStId":
                                bus_nextStId = true;
                            case "plainNo":
                                bus_plainNo = true;

                        }

                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bus_headerCd) {
                            headerCd = xmlpp.getText();
                            // textView.append("headerCd: " + headerCd + "\n");
                            bus_headerCd = false;
                        }
                        if (headerCd.equals("0")) {

                                select.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                if (bus_gpsX) {
                                    count++;
                                    textView.append("-------------------------------------------\n");

                                    gpsX = xmlpp.getText();
                                    textView.append("(" + count + ") gpsX: " + gpsX + "\n");
                                    bus_gpsX = false;
                                }
                                if (bus_gpsY) {
                                    gpsY = xmlpp.getText();
                                    textView.append("(" + count + ") gpsY: " + gpsY + "\n");
                                    bus_gpsY = false;
                                }
                                if (bus_busType) {
                                    busType = xmlpp.getText();
                                    textView.append("(" + count + ") 버스타입 : " + busType + "\n");
                                    bus_busType = false;
                                }
                                if (bus_nextStId) {
                                    nextStId = xmlpp.getText();
                                    textView.append("(" + count + ") 다음 도착예정시간 : " + nextStId + "\n");
                                    bus_nextStId = false;
                                }
                                if (bus_plainNo) {
                                    plainNo = xmlpp.getText();
                                    textView.append("(" + count + ") 차량번호 : " + plainNo + "\n");
                                    bus_plainNo = false;
                                }
                            }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xmlpp.next();
                }
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }

        }


        public String downloadByUrl(String myurl) throws IOException {
            //Java와 Http 통신: HttpURLConnection 클래스를 활용해 데이터를 얻는다.

            HttpURLConnection conn = null;
            // HttpURLConnection conn2 = null;
            BufferedReader buffer_reader;
            // BufferedReader buffer_reader2;
            try {
                //요청 URL, 전달받은 url string 으로 URL 객체를 만듦
                URL url = new URL(myurl);
                //URL url2 = new URL(myurl2);
                conn = (HttpURLConnection) url.openConnection();
                //conn2 = (HttpURLConnection) url2.openConnection();
                conn.setRequestMethod("GET");
                //conn2.setRequestMethod("GET");
                //결과를 InputStream으로 받아서 INputStreamReader, BufferedReader로 캐스팅한다.
                BufferedInputStream buffer = new BufferedInputStream(conn.getInputStream());
                //BufferedInputStream buffer2 = new BufferedInputStream(conn2.getInputStream());
                buffer_reader = new BufferedReader(new InputStreamReader(buffer, "utf-8"));
                //buffer_reader2 = new BufferedReader(new InputStreamReader(buffer2, "utf-8"));
                String line = null;
                //String line2 = null;
                getData = "";
                while ((line = buffer_reader.readLine()) != null) {
                    getData += line;
                }

                return getData;
            } finally {
                //접속 해제
                conn.disconnect();
            }

        }
    }


    public void search(View v) {

        Input = editPhoneNum.getText().toString();
        busNum = Integer.parseInt(Input);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Input = editPhoneNum.getText().toString();
                busNum = Integer.parseInt(Input);

                String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
                String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
                strSrch = busNum + "";

                //가져올 정보를 strUrl에 저장함
                String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;
//
                DownloadWebContent dwc1 = new DownloadWebContent();
                dwc1.execute(strUrl);
                textView.setText("");
                busNumer.setText("");
                busNumer.append("버스번호:");

                busNumer.append(strSrch + "\n");
            }
        });

    }

    /*public void plusBusNumber(View v) {

        busNum += 1;

        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
        strSrch = busNum + "";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;
//
        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
        textView.setText("");
        busNumer.setText("");
        busNumer.append("버스번호:");
        busNumer.append(strSrch + "\n");
    }

    public void minusBusNumber(View v) {

        busNum -= 1;

        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
        strSrch = busNum + "";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;
//
        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
        textView.setText("");
        busNumer.setText("");
        busNumer.append("버스번호:");
        busNumer.append(strSrch + "\n");
    }

    public void resetCurrentBus(View v) {

        //  busNum+=1;
        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
        strSrch = busNum + "";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;

        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
        textView.setText("");
        busNumer.setText("");
        busNumer.append("버스번호:");
        busNumer.append(strSrch + "\n");
    }

    public void plusBaek(View v) {
        busNum += 100;
        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
        strSrch = busNum + "";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;

        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
        textView.setText("");
        busNumer.setText("");
        busNumer.append("버스번호:");
        busNumer.append(strSrch + "\n");
    }

    public void minusBaek(View v) {
        busNum -= 100;
        String serviceUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList";
        String serviceKey = "SkqEVti99R4Q6i221QOBEi4VCEp690K35blE0sXUZyMWQ2NzQYLgjcKh0mMAI2fCGjQ%2FC8nY%2FK0yfwPRCqJnQQ%3D%3D";
        strSrch = busNum + "";

        //가져올 정보를 strUrl에 저장함
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&strSrch=" + strSrch;

        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
        textView.setText("");
        busNumer.setText("");
        busNumer.append("버스번호:");
        busNumer.append(strSrch + "\n");

    }*/


}


