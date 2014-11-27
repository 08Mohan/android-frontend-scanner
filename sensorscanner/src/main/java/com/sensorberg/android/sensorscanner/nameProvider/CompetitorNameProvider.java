package com.sensorberg.android.sensorscanner.nameProvider;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.sdk.cluster.BeaconId;

import java.util.HashMap;
import java.util.Map;

public class CompetitorNameProvider implements NameProvider {

    static Map<String, String> uuidMap = new HashMap<>();
    static {
        uuidMap.put("10A8774D-E5D7-404D-9D25-66ADD4AD1DB3".replace("-", ""), "Movingtracks");
        uuidMap.put("19D5F76A-FD04-5AA3-B16E-E93277163AF6".replace("-", "") ,"Passkit" );
        uuidMap.put("20CAE8A0-A9CF-11E3-A5E2-0800200C9A66".replace("-", ""), "Onyx beacon");
        uuidMap.put("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6".replace("-", ""), "RadBeacon");
        uuidMap.put("61687109-905F-4436-91F8-E602F514C96D".replace("-", "") ,"Bluecats" );
        uuidMap.put("74278BDA-B644-4520-8F0C-720EAF059935".replace("-", "") ,"Glimworm" );
        uuidMap.put("85FC11DD-4CCA-4B27-AFB3-876854BB5C3B".replace("-", ""), "Smartbeacon");
        uuidMap.put("8DEEFBB9-F738-4297-8040-96668BB44281".replace("-", ""), "Roximity");
        uuidMap.put("ACFD065e-C3C0-11E3-9BBE-1A514932AC01".replace("-", "") ,"BlueUp" );
        uuidMap.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D".replace("-", ""), "Estimote");
        uuidMap.put("B0702980-A295-A8AB-F734-031A98A512DE".replace("-", "") ,"RedBear" );
        uuidMap.put("F0018B9B-7509-4C31-A905-1A27D39C003C".replace("-", ""), "Beaconinside");
        uuidMap.put("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0".replace("-", "") ,"Twocanoes" );
        uuidMap.put("EBEFD083-70A2-47C8-9837-E7B5634DF524".replace("-", "") ,"EasiBeacon or Beaconic");
        uuidMap.put("F7826DA6-4FA2-4E98-8024-BC5B71E0893E".replace("-", ""), "kontakt.io");
        uuidMap.put("F2A74FC4-7625-44DB-9B08-CB7E130B2029".replace("-", "") ,"Ubudu" );
        uuidMap.put("D57092AC-DFAA-446C-8EF3-C81AA22815B5".replace("-", "") , "SB Legacy or shopnow");
    }

    @Override
    public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
        String name = uuidMap.get(beaconId.getNormalizedUUIDString());
        if (name != null){
            beaconName.manufacturer = name;
            return false;
        }

        return true;
    }
}
