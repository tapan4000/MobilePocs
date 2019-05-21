package com.example.tapanj.mapsdemo.adapters;

import com.example.tapanj.mapsdemo.integration.MapServiceBuilder;
import com.example.tapanj.mapsdemo.integration.adapters.interfaces.IMapAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGeoCoder {
    //    public static List<Address> getFromLocation(double lat, double lng, int maxResult) {
//
//        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + Locale.getDefault().getCountry(), lat, lng);
//        HttpGet httpGet = new HttpGet(address);
//        HttpClient client = new DefaultHttpClient();
//        client.getParams().setParameter(AllClientPNames.USER_AGENT, "Mozilla/5.0 (Java) Gecko/20081007 java-geocoder");
//        client.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 5 * 1000);
//        client.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 25 * 1000);
//        HttpResponse response;
//
//        List<Address> retList = null;
//
//        try {
//            response = client.execute(httpGet);
//            HttpEntity entity = response.getEntity();
//            String json = EntityUtils.toString(entity, "UTF-8");
//
//            JSONObject jsonObject = new JSONObject(json);
//
//            retList = new ArrayList<Address>();
//
//            if ("OK".equalsIgnoreCase(jsonObject.getString("operationState"))) {
//                JSONArray results = jsonObject.getJSONArray("results");
//                if (results.length() > 0) {
//                    for (int i = 0; i < results.length() && i < maxResult; i++) {
//                        JSONObject result = results.getJSONObject(i);
//                        //Log.e(MyGeocoder.class.getName(), result.toString());
//                        Address addr = new Address(Locale.getDefault());
//                        // addr.setAddressLine(0, result.getString("formatted_address"));
//
//                        JSONArray components = result.getJSONArray("address_components");
//                        String streetNumber = "";
//                        String route = "";
//                        for (int a = 0; a < components.length(); a++) {
//                            JSONObject component = components.getJSONObject(a);
//                            JSONArray types = component.getJSONArray("types");
//                            for (int j = 0; j < types.length(); j++) {
//                                String type = types.getString(j);
//                                if (type.equals("locality")) {
//                                    addr.setLocality(component.getString("long_name"));
//                                } else if (type.equals("street_number")) {
//                                    streetNumber = component.getString("long_name");
//                                } else if (type.equals("route")) {
//                                    route = component.getString("long_name");
//                                }
//                            }
//                        }
//                        addr.setAddressLine(0, route + " " + streetNumber);
//
//                        addr.setLatitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
//                        addr.setLongitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
//                        retList.add(addr);
//                    }
//                }
//            }
//
//
//        } catch (ClientProtocolException e) {
//            Log.e(MyGeocoder.class.getName(), "Error calling Google geocode webservice.", e);
//        } catch (IOException e) {
//            Log.e(MyGeocoder.class.getName(), "Error calling Google geocode webservice.", e);
//        } catch (JSONException e) {
//            Log.e(MyGeocoder.class.getName(), "Error parsing Google geocode webservice response.", e);
//        }
//
//        return retList;
//    }

    public static void getLocationAddress(double lat, double lng, int maxResultCount){
        IMapAdapter mapAdapter = MapServiceBuilder.buildService(IMapAdapter.class);
        String latLngCsv = lat + "," + lng;
        Call<String> groupListRequest = mapAdapter.getAddresses(latLngCsv, false, "en-US");
        groupListRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String responseData = response.body();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                String exceptionMessage = t.getMessage();
            }
        });
    }
}
