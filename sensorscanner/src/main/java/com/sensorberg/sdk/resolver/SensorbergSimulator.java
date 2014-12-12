package com.sensorberg.sdk.resolver;

import android.content.Context;
import android.os.Parcelable;
import android.widget.Toast;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.sdk.SensorbergService;
import com.sensorberg.sdk.bootstrapper.SensorbergApplicationBootstrapper;
import com.sensorberg.sdk.exception.SdkException;
import com.sensorberg.sdk.internal.AndroidPlattform;
import com.sensorberg.sdk.internal.HttpClientTransport;
import com.sensorberg.sdk.internal.Plattform;
import com.sensorberg.sdk.internal.Transport;
import com.sensorberg.sdk.presenter.Presentation;
import com.sensorberg.sdk.presenter.PresentationConfiguration;
import com.sensorberg.sdk.presenter.Presenter;
import com.sensorberg.sdk.presenter.PresenterConfiguration;
import com.sensorberg.sdk.presenter.PresenterFactory;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;
import com.sensorberg.sdk.settings.Settings;

import java.util.List;

public class SensorbergSimulator implements ResolverListener {
    final Resolver resolver;
    final private Transport transport;
    private final Context context;
    public static SensorbergApplicationBootstrapper bootstrapper;
    private final Presenter presenter;
    final android.os.Handler handler = new android.os.Handler();
    private final Settings settings;


    public SensorbergSimulator(final Context context, String apiToken) {
        this.context = context;

        Plattform platforn = new AndroidPlattform(context){
            public void postToServiceDelayed(long delay, int type, Parcelable what, boolean surviveReboot) {
                switch (type){
                    case SensorbergService.GENERIC_TYPE_RETRY_RESOLVE_SCANEVENT: {
                        final ResolutionConfiguration conf = (ResolutionConfiguration) what;
                        Toast.makeText(context, "retying request:" + conf.retry, Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable(){
                                                    @Override
                                                    public void run() {
                                                        resolver.retry(conf);
                                                    }
                                                }, settings.getMillisBeetweenRetries());
                        return;
                    }
                }
                super.postToServiceDelayed(delay, type, what, surviveReboot);
            }

        };
        settings = new Settings(platforn, context.getSharedPreferences("simulated_events", Context.MODE_PRIVATE));
        transport = new HttpClientTransport(platforn, settings);
        transport.setApiToken(apiToken);
        ResolverConfiguration resolverConf = new ResolverConfiguration();
        resolverConf.setApiToken(apiToken);
        resolver = new Resolver(resolverConf, platforn);
        resolver.addResolverListener(this);
        PresenterConfiguration presenterConf = new PresenterConfiguration(R.drawable.ic_launcher);
        presenter = new Presenter(presenterConf, platforn);
    }

    public void simulateEvent(BeaconScanObject object, ScanEventType entry) {
        ScanEvent scanEvent = new ScanEvent.Builder()
                .withEventMask(entry.getMask())
                .withBeaconId(object.beaconId)
                .withEventTime(System.currentTimeMillis())
                .withName("simulated event")
                .build();
        ResolutionConfiguration configuration = new ResolutionConfiguration(scanEvent);
        Resolution resolution = new Resolution(resolver, configuration, transport);
        resolver.startResolution(resolution);
        Toast.makeText(context, "starting request", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResolutionFailed(Resolution resolution, Throwable throwable) {
        Toast.makeText(context, "Resolution Failed" + throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResolutionsFinished(Resolution resolution, List<BeaconEvent> beaconEvents) {
        if (beaconEvents.size() == 0){
            Toast.makeText(context, "Resolution finished without action" , Toast.LENGTH_LONG).show();
        } else {
            for (BeaconEvent beaconEvent : beaconEvents) {
                PresentationConfiguration presentationConf = new PresentationConfiguration(beaconEvent);
                Presentation presentation = PresenterFactory.newPresentation(presenter, presentationConf);
                try {
                    presentation.start();
                } catch (SdkException e) {
                    Toast.makeText(context, "something went wrong" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                bootstrapper.presentBeaconEvent(beaconEvent);
                Toast.makeText(context, "Resolution finished with action" , Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResolutionFinished(Resolution resolution, BeaconEvent beaconEvent) {


    }
}
