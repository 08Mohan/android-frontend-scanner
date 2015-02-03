[![Build Status](https://travis-ci.org/sensorberg-dev/android-frontend-scanner.svg?branch=master)](https://travis-ci.org/sensorberg-dev/android-frontend-scanner)

# android-frontend-scanner
Use the scanner contained in our SDK to show beacons in a ListView or as a Beacon Ranger.

<img src="https://raw.githubusercontent.com/sensorberg-dev/android-frontend-scanner/master/documentation/screenshots/screenshot0.png" width="250">
<img src="https://raw.githubusercontent.com/sensorberg-dev/android-frontend-scanner/master/documentation/screenshots/screenshot1.png" width="250">
<img src="https://raw.githubusercontent.com/sensorberg-dev/android-frontend-scanner/master/documentation/screenshots/screenshot2.png" width="250">
<img src="https://raw.githubusercontent.com/sensorberg-dev/android-frontend-scanner/master/documentation/screenshots/screenshot3.png" width="250">
<img src="https://raw.githubusercontent.com/sensorberg-dev/android-frontend-scanner/master/documentation/screenshots/screenshot4.png" width="250">

If you want to use the scanner in your own application, build the aar and integrate it as a module in your project as described [here](http://tools.android.com/tech-docs/new-build-system/tips#TOC-Handling-transitive-dependencies-for-local-artifacts-jars-and-aar-)

#How to use:

##customizations of the SensorScanner

This applies to the use in a ListView or standalone

###RuntimeFilter
Add your own Implementations. They filter the results before every UI update. 

If you want to filter by RSSI, distance, sample count use a Filter like this.

###BeaconFilter
Add your own Implementation. These Filters are only applied when a new Beacon is detected.

If you want to filter by ProximityUUID, ProximityMajor or ProximityMinor use a filter like this.

###sample window
The sample window defines the time that the scanner waits to collect samples before updating the UI. New beacons are added immediatly.

###settings
You propably don´t need to change anything here. The exit timeout defines the time, which has to pass without any new samples, before a beacon is dropped from the list.


##ListFragment

Look extend ```BeaconScanFragmentWithTotalCount``` or ```BeaconScanFragment``` and implement ```getFormatter()``` and ```getScanner()```

Customize the Scanner to your needs. The TechnicalScannerFragment has a extensively customized Scanner:

```java
	@Override
    protected SensorScanner getScanner() {
        if(scanner == null){
            scanner = new SensorScanner(getActivity())
                    .addFilter(this)
                    .addNameProvider(new CompetitorNameProvider())
                    .addNameProvider(new SensorbergNameProvider())
                    .addNameProvider(new NameProvider() {
                        @Override
                        public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
                            if (beaconName.manufacturer == null){
                                beaconName.manufacturer = "Unknown";
                            }
                            return true;
                        }
                    });
            final int rssiFilter = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_LIMIT_RSSI);
            if (rssiFilter != 0) {
                scanner.addRuntimeFilter(new RuntimeFilter() {
                    @Override
                    public boolean matches(BeaconScanObject beaconScanObject) {
                        return beaconScanObject.getLastDistanceCalculation().rssi.min > -rssiFilter;
                    }
                });
            }
            final int distanceFilter = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_LIMIT_METERS);
            if (distanceFilter != 0) {
                scanner.addRuntimeFilter(new RuntimeFilter() {
                    @Override
                    public boolean matches(BeaconScanObject beaconScanObject) {
                        return beaconScanObject.getLastDistanceCalculation().distanceInMeters < distanceFilter;
                    }
                });
            }
        }
        return scanner;
    }
```

The ScannedBeaconListAdapter.ContentFormatter takes care of preparing the cells in your ListView for the ScanItems:

```java
	@Override
    protected ScannedBeaconListAdapter.ContentFormatter getFormatter() {
        return new RangeFormatter(Constants.MAX_BEACON_AGE) {
            @Override
            public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder viewHolder) {
                super.apply(beaconScanObject, viewHolder);
                viewHolder.textviewFirstLine.setText(beaconScanObject.beaconName.name);
                viewHolder.textviewSecondline.setText("Vendor: " + beaconScanObject.beaconName.manufacturer);
                viewHolder.textviewThirdline1.setText(String.format(majorFormat, beaconScanObject.beaconId.getMajorId()));
                viewHolder.textviewThirdline2.setText(String.format(minorFormat, beaconScanObject.beaconId.getMinorId()));
            }
        };
    }
```

##Standalone

You can also use the SensorScanner standalone.


