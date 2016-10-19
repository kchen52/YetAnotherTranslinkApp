package com.kchen52.yetanothertranslinkapp;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BusListActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;

    private ListView listView;
    private String busesRequested;
    private Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_bus_list);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        busesRequested = sharedPref.getString(getString(R.string.saved_buses_requested), getString(R.string.saved_buses_requested_default));

        listView = (ListView)findViewById(R.id.busListView);

        applicationContext = this;
        CustomAdapter adapter = new CustomAdapter(applicationContext, getBusListArray(""));
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                openSearchBar();
                return true;
        }
        return true;
    }
    private void openSearchBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);


        EditText searchEditText = (EditText) actionBar.getCustomView().findViewById(R.id.etSearch);
        searchEditText.setHint("Input text here");
        searchEditText.addTextChangedListener(new SearchWatcher());
        searchEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, 0);
    }

    private void closeSearchBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
    }

    private class SearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            CustomAdapter adapter = new CustomAdapter(applicationContext, getBusListArray(s.toString()));
            listView.setAdapter(adapter);
        }
    }

    private Model[] getBusListArray(String searchTerm) {
        String[] buses = busesRequested.split(", ");

        List<Model> busList = new ArrayList<>();
        for (String busNumberAndDestination : busDestinationList) {
            if (busNumberAndDestination.contains(searchTerm.toUpperCase())) {
                busList.add(createModel(busNumberAndDestination, buses));
            }
        }
        return busList.toArray(new Model[busList.size()]);
    }

    // Creates the model and sets checkbox the bus was checked before
    private Model createModel(String busNumberAndDestinations, String[] checkedBuses) {
        String busNumber = busNumberAndDestinations.split(" - ")[0];
        int checked = 0;
        for (String bus : checkedBuses) {
            if (bus.equals(busNumber)) {
                checked = 1;
                break;
            }
        }
        return new Model(busNumberAndDestinations, checked);
    }

    private String[] busDestinationList = {
            "002 - MACDONALD/DOWNTOWN",
            "003 - MAIN/DOWNTOWN",
            "004 - POWELL/DOWNTOWN/UBC",
            "005 - ROBSON/DOWNTOWN",
            "006 - DAVIE/DOWNTOWN",
            "007 - NANAIMO STN/DUNBAR",
            "008 - FRASER/DOWNTOWN",
            "009 - BDRY/COMM-BWY/GRAN/ALMA/UBC",
            "010 - GRANVILLE/DOWNTOWN",
            "014 - HASTINGS/UBC",
            "015 - CAMBIE/OLYMPIC VILLAGE STN",
            "016 - 29TH AVENUE STN/ARBUTUS",
            "017 - OAK/DOWNTOWN",
            "019 - METROTOWN STN/STANLEY PARK",
            "020 - VICTORIA/DOWNTOWN",
            "022 - KNIGHT/DOWNTOWN",
            "025 - BRENTWOOD STN/UBC",
            "026 - JOYCE STN/29TH AVENUE STN",
            "027 - KOOTENAY LOOP/JOYCE STN",
            "028 - CAP UNIVERSIT/PHIBBS EXCH/JOYCE STN",
            "029 - ELLIOTT/29TH AVENUE STN",
            "032 - DUNBAR/DOWNTOWN",
            "033 - 29 AVE STN/ UBC",
            "041 - JOYCE STN/CROWN/UBC",
            "043 - JOYCE STN/UBC",
            "044 - UBC/DOWNTOWN",
            "049 - METROTOWN STN/DUNBAR LOOP/UBC",
            "050 - WATERFRONT STN/FALSE CREEK SOUTH",
            "084 - UBC/VCC STATION",
            "096 - GUILDFORD EX/ NEWTON EX (B-LINE)",
            "097 - COQUITLAM STN/LOUGHEED STN (B-LINE)",
            "099 - COMMERCIAL-BROADWAY/UBC (B-LINE)",
            "100 - 22ND ST STN/MARPOLE LOOP",
            "101 - LOUGHEED STN/22ND ST STN",
            "104 - 22ND ST STN/ANNACIS ISLAND",
            "106 - NEW WESTMINSTER STN/METROTOWN STN",
            "110 - LOUGHEED STN/METROTOWN STN",
            "112 - EDMONDS STN/NEW WEST STN",
            "116 - EDMONDS STN/METROTOWN STN",
            "123 - NEW WEST STN/BRENTWOOD STN",
            "125 - PATTERSON STN/BCIT",
            "128 - BRAID STN/22ND STREET STN",
            "129 - PATTERSON STN/EDMONDS STN",
            "130 - METROTOWN/HASTINGS/KOOTENAY/CAP U",
            "134 - LAKE CITY STN/BRENTWOOD STN",
            "135 - SFU/BURRARD STN",
            "136 - LOUGHEED STN/BRENTWOOD STN",
            "143 - COQUITLAM STN/SFU",
            "144 - SFU/METROTOWN STN",
            "145 - SFU/PRODUCTION STN",
            "151 - COQUITLAM STN/LOUGHEED STN",
            "152 - COQUITLAM STN/LOUGHEED STN",
            "153 - COQUITLAM REC CTR/BRAID STN",
            "155 - BRAID STN/22ND STREET STN",
            "156 - BRAID STN/LOUGHEED STN",
            "157 - COQUITLAM REC CENTRE/LOUGHEED STN",
            "159 - PORT COQUITLAM STN/BRAID STN",
            "160 - PORT COQUITLAM STN/VANCOUVER",
            "169 - COQUITLAM STN/BRAID STN",
            "178 - COQUITLAM STN/PORT MOODY STN",
            "188 - COQUITLAM STN/PORT COQUITLAM STN",
            "190 - COQUITLAM STN/VANCOUVER",
            "209 - UPPER LYNN VALLEY/VANCOUVER",
            "210 - UPPER LYNN VALLEY/VANCOUVER",
            "211 - SEYMOUR/PHIBBS EXCH/VANCOUVER",
            "212 - DEEP COVE/PHIBBS EXCH",
            "214 - BLUERIDGE/PHIBBS EXCH/VANCOUVER",
            "227 - LYNN VALLEY CENTRE/PHIBBS EXCHANGE",
            "228 - LYNN VALLEY/LONSDALE QUAY",
            "229 - LYNN VALLEY/LONSDALE QUAY",
            "230 - UPPER LONSDALE/LONSDALE QUAY",
            "231 - HARBOURSIDE/LONSDALE QUAY",
            "232 - GROUSE MOUNTAIN/PHIBBS EXCH",
            "236 - GROUSE MOUNTAIN/LONSDALE QUAY",
            "239 - CAPILANO UNIVERSITY/PARK ROYAL",
            "240 - 15TH STREET/VANCOUVER",
            "241 - UPPER LONSDALE/VANCOUVER",
            "242 - LYNN VALLEY/VANCOUVER",
            "246 - LONSDALE QUAY/HIGHLAND/VANCOUVER",
            "247 - UPPER CAPILANO/GROUSE/VANCOUVER",
            "250 - HORSESHOE BAY/DUNDARAVE/VANCOUVER",
            "251 - QUEENS/PARK ROYAL",
            "252 - INGLEWOOD/PARK ROYAL",
            "253 - CAULFEILD/VANCOUVER/PARK ROYAL",
            "254 - BRITISH PROPERTIES/PARK ROYAL/VAN",
            "255 - DUNDARAVE/CAPILANO UNIVERSITY",
            "256 - FOLKSTONE WY/WHITBY ESTATE/SPURAWAY",
            "257 - HORSESHOE BAY/VANCOUVER EXPRESS",
            "258 - UBC/WEST VANCOUVER",
            "259 - LIONS BAY/HORSESHOE BAY",
            "301 - NEWTON EXCHANGE/BRIGHOUSE STATION",
            "311 - SCOTTSDALE/BRIDGEPORT STN",
            "312 - SCOTTSDALE/SCOTT ROAD STN",
            "314 - SURREY CENTRAL/SUNBURY",
            "316 - SURREY CENTRAL STN/SCOTTSDALE",
            "319 - SCOTT ROAD STN/NEWTON EXCHA",
            "320 - LANGLEY/FLEETWOOD/SURREY CTRL STN",
            "321 - WHITE ROCK/NEWTON/SURREY CTRL STN",
            "323 - NEWTON EXCH/SURREY CENTRAL STN",
            "324 - NEWTON EXCH/SURREY CENTRAL STN",
            "325 - NEWTON EXCH/SURREY CENTRAL STN",
            "326 - GUILDFORD/SURREY CENTRAL STN",
            "329 - SURREY CENTRAL STN/SCOTTSDALE",
            "335 - NEWTON/SURREY CENTRAL STN",
            "337 - FRASER HEIGHTS/GUILDFORD/SURREY CTR",
            "340 - SCOTTSDALE/22ND ST STN",
            "341 - GUILDFORD/NEWTON EXCH",
            "342 - LANGLEY CENTRE/NEWTON EXCHANGE",
            "345 - KING GEORGE STN/WHITE ROCK CENTRE",
            "351 - CRESCENT BEACH/BRIDGEPORT STN",
            "352 - OCEAN PARK /BRIDGEPORT STN",
            "354 - WHITE ROCK SOUTH/BRIDGEPORT STATION",
            "364 - LANGLEY CTR/ SCOTTSDALE EX",
            "375 - WHITE ROCK/WHITE ROCK STH/GUILDFORD",
            "388 - 22ST STN/CARVOLTH EXCH",
            "391 - SCOTTSDALE/SCOTT ROAD STN",
            "393 - NEWTON EXCH/SURREY CENTRAL STN",
            "394 - WHITE ROCK/KING GEORGE STN EXPRESS",
            "395 - LANGLEY CENTRE/KING GEORGE STN",
            "401 - ONE ROAD/GARDEN CITY",
            "402 - TWO ROAD/BRIGHOUSE STATION",
            "403 - BRIDGEPORT STN/THREE ROAD",
            "404 - FOUR ROAD/BRIGHOUSE STATION",
            "405 - FIVE ROAD/CAMBIE",
            "407 - GILBERT/BRIDGEPORT",
            "410 - 22ND ST STN/QUEENSBOROUGH/RAILWAY",
            "430 - METROTOWN/BRIGHOUSE STATION",
            "480 - UBC/BRIDGEPORT STN",
            "501 - LANGLEY CENTRE/SURREY CENTRAL STN",
            "502 - LANGLEY CENTRE/SURREY CENTRAL STN",
            "503 - ALDERGROVE/SURREY CENTRAL STN",
            "509 - WALNUT GROVE/SURREY CENTRAL STN",
            "531 - WHITE ROCK CENTRE/WILLOWBROOK",
            "555 - CARVOLTH EXCH / LOUGHEED STN",
            "590 - LANGLEY SOUTH/LANGLEY CENTRE",
            "595 - MAPLE MEADOWS STN/LANGLEY CENTRE",
            "601 - SOUTH DELTA/BOUNDARY BAY/BRIDGEPORT",
            "602 - TSAWWASSEN HEIGHTS/BRIDGEPORT",
            "603 - BEACH GROVE/BRIDGEPORT",
            "604 - ENGLISH BLUFF/BRIDGEPORT",
            "606 - LADNER RING",
            "608 - LADNER RING",
            "609 - TSAWWASSEN FIRST NATION/SOUTH DELTA",
            "620 - TSAWWASSEN FERRY/BRIDGEPORT STATION",
            "640 - LADNER EXCH/SCOTT ROAD STN",
            "701 - HANEY/MAPLE RIDGE EAST/COQ STN",
            "791 - HANEY PLACE/BRAID STN",
            "804 - HOLY CROSS SCHOOL",
            "807 - SCHOOL SPECIAL",
            "828 - KWANTLEN PARK SCHOOL",
            "840 - BROOKSWOOD SCHOOL",
            "848 - PORT MOODY SS",
            "855 - ELGIN PARK SCHOOL SPECIAL",
            "861 - RIVERSIDE SCHOOL",
            "863 - TERRY FOX/ARCH CARNEY",
            "864 - LORD TWEEDSMUIR SCHOOL",
            "865 - ROBERTSON",
            "867 - HERITAGE WOODS SCHOOL",
            "880 - WINDSOR SCHOOL SPECIALS",
            "881 - CARSON GRAHAM SCHOOL SPECIALS",
            "C1 - HASTINGS & GILMORE/KOOTENAY LOOP",
            "C2 - CAPITOL HILL/HASTINGS AT GILMORE",
            "C3 - QUAYSIDE/VICTORIA HILL",
            "C4 - UPTOWN/NEW WESTMINSTER STATION",
            "C5 - ROYAL OAK STN/EDMONDS STN",
            "C6 - METROTOWN STN/SUNCREST",
            "C7 - METROTOWN STN/EDMONDS STN",
            "C9 - NEW WESTMINSTER STN/LOUGHEED STN",
            "C10 - BLUEWATER/SNUG COVE",
            "C11 - EAGLE CLIFF/SNUG COVE",
            "C12 - LIONS BAY/CAULFEILD",
            "C15 - INDIAN RIVER/PHIBBS EXCH",
            "C18 - WEST MALL/UBC LOOP",
            "C19 - ALMA/SPANISH BANKS",
            "C20 - TOTEM PARK/UBC LOOP",
            "C21 - YALETOWN/BEACH",
            "C23 - YALETOWN/DAVIE/MAIN ST STN",
            "C24 - PORT MOODY STN/LOUGHEED STN",
            "C25 - IOCO/PORT MOODY STN",
            "C26 - BELCARRA/PORT MOODY STN",
            "C27 - COQUITLAM STN/PORT MOODY STN",
            "C28 - COQUITLAM STN/PORT MOODY STN",
            "C29 - COQUITLAM STN/PARKWAY BOULEVARD",
            "C30 - LAFARGE PARK/COQUITLAM STN",
            "C36 - PORT COQ STN/PORT COQ SOUTH",
            "C37 - PORT COQ STN/PRAIRIE/RIVERSIDE",
            "C38 - COQ STN/RIVER SPR/PRAIRIE/POCO",
            "C40 - MERIDIAN/PORT COQUITLAM STN",
            "C41 - MEADOWTOWN/MAPLE MDWS STN/P MDW CTR",
            "C43 - HANEY PL/MAPLE MDWS STN/MEADOWTOWN",
            "C44 - HANEY PL/MAPLE MDWS STN/MEADOWTOWN",
            "C45 - COTTONWOOD/HANEY PLACE",
            "C46 - ALBION/HANEY PLACE",
            "C47 - ALOUETTE/HANEY PLACE",
            "C48 - THORNHILL/HANEY PLACE",
            "C49 - RUSKIN/HANEY PLACE",
            "C50 - OCEAN PARK/PEACE ARCH HOSPITAL",
            "C51 - OCEAN PARK/WHITE ROCK CENTRE",
            "C52 - SEASIDE/WHITE ROCK CENTRE",
            "C53 - CRANLEY DRIVE/WHITE ROCK CENTRE",
            "C60 - LANGLEY CENTRE/LANGLEY HOSPITAL",
            "C61 - LANGLEY CENTRE/BROOKSWOOD",
            "C62 - LANGLEY CENTRE/WALNUT GROVE",
            "C63 - LANGLEY CENTRE/FERNRIDGE",
            "C64 - LANGLEY CTR/WILLOWBROOK",
            "C70 - CLOVERDALE/WILLOWBROOK",
            "C71 - SURREY CNTRL/SCOTT RD STN",
            "C73 - GUILDFORD/ SURREY CNTRL STN",
            "C75 - NEWTON EX/SCOTTSDALE",
            "C76 - SCOTTSDALE/LADNER EX",
            "C84 - ENGLISH BLUFF/SOUTH DELTA REC CENTR",
            "C86 - LADNER SOUTH/LADNER EXCH",
            "C87 - EAST LADNER/LADNER EXCH",
            "C88 - LADNER NORTH/LADNER EXCH",
            "C89 - BOUNDARY BAY/SOUTH DELTA REC CENTRE",
            "C92 - BRIDGEPORT STATION/SEA ISLAND SOUTH",
            "C93 - RIVERPORT/STEVESTON",
            "C94 - RICHMOND OVAL/BRIGHOUSE STN",
            "C96 - EAST CAMBIE/BRIGHOUSE STATION",
            "C98 - KINGSWOOD/22ND ST STN",
            "N8 - DOWNTOWN/FRASER NIGHTBUS",
            "N9 - DOWNTOWN/COQUITLAM STN NIGHTBUS",
            "N10 - DOWNTOWN/RICHMOND NIGHTBUS",
            "N15 - DOWNTOWN/CAMBIE NIGHTBUS",
            "N17 - DOWNTOWN/UBC NIGHTBUS",
            "N19 - DOWNTOWN/SURREY CNTRL STN NIGHTBUS",
            "N20 - DOWNTOWN/VICTORIA NIGHTBUS",
            "N22 - DOWNTOWN/MACDONALD NIGHTBUS",
            "N24 - DOWNTOWN/ LYNN VALLEY NIGHTBUS",
            "N35 - DOWNTOWN/SFU NIGHTBUS"
    };
}
