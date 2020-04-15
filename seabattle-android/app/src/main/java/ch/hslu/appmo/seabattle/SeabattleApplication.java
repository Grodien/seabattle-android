package ch.hslu.appmo.seabattle;

import android.app.Application;

import com.parse.Parse;

import ch.hslu.appmo.seabattle.network.ParseClient;

public class SeabattleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("axfe93yHtwMdPoeaXT8iXkyuk1IEI8RDqj9hy9Io")
                .clientKey("dFE96lJ829BgmfI2ROyqwwWo2Zd8nM1EPSPwnBLL")
                .server("https://parseapi.back4app.com")
                .build());
    }
}
