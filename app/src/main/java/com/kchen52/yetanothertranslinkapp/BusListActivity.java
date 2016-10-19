package com.kchen52.yetanothertranslinkapp;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

public class BusListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SharedPreferences sharedPref;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_bus_list);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String busesRequested = sharedPref.getString(getString(R.string.saved_buses_requested), getString(R.string.saved_buses_requested_default));

        ListView listView = (ListView)findViewById(R.id.busListView);

        CustomAdapter adapter = new CustomAdapter(this, getBusListArray(busesRequested));
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        /*inflater.inflate(R.menu.example, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        if (searchView == null) {
            Log.d("DEBUG", "searchView should not be null, yet it is...");
        }
        setupSearchView(searchItem);*/
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
        searchEditText.setText("temp text");
        searchEditText.requestFocus();

    }

    private void setupSearchView(MenuItem searchItem) {
        searchView.setIconifiedByDefault(false);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            searchView.setSearchableInfo(info);
        }
        searchView.setOnQueryTextListener(this);
    }





    private Model[] getBusListArray(String savedCheckedBuses) {
        String[] buses = savedCheckedBuses.split(", ");

        Model[] busList = new Model[230];
        busList[0] = createModel("002 - MACDONALD/DOWNTOWN", buses);
        busList[1] = createModel("003 - MAIN/DOWNTOWN", buses);
        busList[2] = createModel("004 - POWELL/DOWNTOWN/UBC", buses);
        busList[3] = createModel("005 - ROBSON/DOWNTOWN", buses);
        busList[4] = createModel("006 - DAVIE/DOWNTOWN", buses);
        busList[5] = createModel("007 - NANAIMO STN/DUNBAR", buses);
        busList[6] = createModel("008 - FRASER/DOWNTOWN", buses);
        busList[7] = createModel("009 - BDRY/COMM-BWY/GRAN/ALMA/UBC", buses);
        busList[8] = createModel("010 - GRANVILLE/DOWNTOWN", buses);
        busList[9] = createModel("014 - HASTINGS/UBC", buses);
        busList[10] = createModel("015 - CAMBIE/OLYMPIC VILLAGE STN", buses);
        busList[11] = createModel("016 - 29TH AVENUE STN/ARBUTUS", buses);
        busList[12] = createModel("017 - OAK/DOWNTOWN", buses);
        busList[13] = createModel("019 - METROTOWN STN/STANLEY PARK", buses);
        busList[14] = createModel("020 - VICTORIA/DOWNTOWN", buses);
        busList[15] = createModel("022 - KNIGHT/DOWNTOWN", buses);
        busList[16] = createModel("025 - BRENTWOOD STN/UBC", buses);
        busList[17] = createModel("026 - JOYCE STN/29TH AVENUE STN", buses);
        busList[18] = createModel("027 - KOOTENAY LOOP/JOYCE STN", buses);
        busList[19] = createModel("028 - CAP UNIVERSIT/PHIBBS EXCH/JOYCE STN", buses);
        busList[20] = createModel("029 - ELLIOTT/29TH AVENUE STN", buses);
        busList[21] = createModel("032 - DUNBAR/DOWNTOWN", buses);
        busList[22] = createModel("033 - 29 AVE STN/ UBC", buses);
        busList[23] = createModel("041 - JOYCE STN/CROWN/UBC", buses);
        busList[24] = createModel("043 - JOYCE STN/UBC", buses);
        busList[25] = createModel("044 - UBC/DOWNTOWN", buses);
        busList[26] = createModel("049 - METROTOWN STN/DUNBAR LOOP/UBC", buses);
        busList[27] = createModel("050 - WATERFRONT STN/FALSE CREEK SOUTH", buses);
        busList[28] = createModel("084 - UBC/VCC STATION", buses);
        busList[29] = createModel("096 - GUILDFORD EX/ NEWTON EX (B-LINE)", buses);
        busList[30] = createModel("097 - COQUITLAM STN/LOUGHEED STN (B-LINE)", buses);
        busList[31] = createModel("099 - COMMERCIAL-BROADWAY/UBC (B-LINE)", buses);
        busList[32] = createModel("100 - 22ND ST STN/MARPOLE LOOP", buses);
        busList[33] = createModel("101 - LOUGHEED STN/22ND ST STN", buses);
        busList[34] = createModel("104 - 22ND ST STN/ANNACIS ISLAND", buses);
        busList[35] = createModel("106 - NEW WESTMINSTER STN/METROTOWN STN", buses);
        busList[36] = createModel("110 - LOUGHEED STN/METROTOWN STN", buses);
        busList[37] = createModel("112 - EDMONDS STN/NEW WEST STN", buses);
        busList[38] = createModel("116 - EDMONDS STN/METROTOWN STN", buses);
        busList[39] = createModel("123 - NEW WEST STN/BRENTWOOD STN", buses);
        busList[40] = createModel("125 - PATTERSON STN/BCIT", buses);
        busList[41] = createModel("128 - BRAID STN/22ND STREET STN", buses);
        busList[42] = createModel("129 - PATTERSON STN/EDMONDS STN", buses);
        busList[43] = createModel("130 - METROTOWN/HASTINGS/KOOTENAY/CAP U", buses);
        busList[44] = createModel("134 - LAKE CITY STN/BRENTWOOD STN", buses);
        busList[45] = createModel("135 - SFU/BURRARD STN", buses);
        busList[46] = createModel("136 - LOUGHEED STN/BRENTWOOD STN", buses);
        busList[47] = createModel("143 - COQUITLAM STN/SFU", buses);
        busList[48] = createModel("144 - SFU/METROTOWN STN", buses);
        busList[49] = createModel("145 - SFU/PRODUCTION STN", buses);
        busList[50] = createModel("151 - COQUITLAM STN/LOUGHEED STN", buses);
        busList[51] = createModel("152 - COQUITLAM STN/LOUGHEED STN", buses);
        busList[52] = createModel("153 - COQUITLAM REC CTR/BRAID STN", buses);
        busList[53] = createModel("155 - BRAID STN/22ND STREET STN", buses);
        busList[54] = createModel("156 - BRAID STN/LOUGHEED STN", buses);
        busList[55] = createModel("157 - COQUITLAM REC CENTRE/LOUGHEED STN", buses);
        busList[56] = createModel("159 - PORT COQUITLAM STN/BRAID STN", buses);
        busList[57] = createModel("160 - PORT COQUITLAM STN/VANCOUVER", buses);
        busList[58] = createModel("169 - COQUITLAM STN/BRAID STN", buses);
        busList[59] = createModel("178 - COQUITLAM STN/PORT MOODY STN", buses);
        busList[60] = createModel("188 - COQUITLAM STN/PORT COQUITLAM STN", buses);
        busList[61] = createModel("190 - COQUITLAM STN/VANCOUVER", buses);
        busList[62] = createModel("209 - UPPER LYNN VALLEY/VANCOUVER", buses);
        busList[63] = createModel("210 - UPPER LYNN VALLEY/VANCOUVER", buses);
        busList[64] = createModel("211 - SEYMOUR/PHIBBS EXCH/VANCOUVER", buses);
        busList[65] = createModel("212 - DEEP COVE/PHIBBS EXCH", buses);
        busList[66] = createModel("214 - BLUERIDGE/PHIBBS EXCH/VANCOUVER", buses);
        busList[67] = createModel("227 - LYNN VALLEY CENTRE/PHIBBS EXCHANGE", buses);
        busList[68] = createModel("228 - LYNN VALLEY/LONSDALE QUAY", buses);
        busList[69] = createModel("229 - LYNN VALLEY/LONSDALE QUAY", buses);
        busList[70] = createModel("230 - UPPER LONSDALE/LONSDALE QUAY", buses);
        busList[71] = createModel("231 - HARBOURSIDE/LONSDALE QUAY", buses);
        busList[72] = createModel("232 - GROUSE MOUNTAIN/PHIBBS EXCH", buses);
        busList[73] = createModel("236 - GROUSE MOUNTAIN/LONSDALE QUAY", buses);
        busList[74] = createModel("239 - CAPILANO UNIVERSITY/PARK ROYAL", buses);
        busList[75] = createModel("240 - 15TH STREET/VANCOUVER", buses);
        busList[76] = createModel("241 - UPPER LONSDALE/VANCOUVER", buses);
        busList[77] = createModel("242 - LYNN VALLEY/VANCOUVER", buses);
        busList[78] = createModel("246 - LONSDALE QUAY/HIGHLAND/VANCOUVER", buses);
        busList[79] = createModel("247 - UPPER CAPILANO/GROUSE/VANCOUVER", buses);
        busList[80] = createModel("250 - HORSESHOE BAY/DUNDARAVE/VANCOUVER", buses);
        busList[81] = createModel("251 - QUEENS/PARK ROYAL", buses);
        busList[82] = createModel("252 - INGLEWOOD/PARK ROYAL", buses);
        busList[83] = createModel("253 - CAULFEILD/VANCOUVER/PARK ROYAL", buses);
        busList[84] = createModel("254 - BRITISH PROPERTIES/PARK ROYAL/VAN", buses);
        busList[85] = createModel("255 - DUNDARAVE/CAPILANO UNIVERSITY", buses);
        busList[86] = createModel("256 - FOLKSTONE WY/WHITBY ESTATE/SPURAWAY", buses);
        busList[87] = createModel("257 - HORSESHOE BAY/VANCOUVER EXPRESS", buses);
        busList[88] = createModel("258 - UBC/WEST VANCOUVER", buses);
        busList[89] = createModel("259 - LIONS BAY/HORSESHOE BAY", buses);
        busList[90] = createModel("301 - NEWTON EXCHANGE/BRIGHOUSE STATION", buses);
        busList[91] = createModel("311 - SCOTTSDALE/BRIDGEPORT STN", buses);
        busList[92] = createModel("312 - SCOTTSDALE/SCOTT ROAD STN", buses);
        busList[93] = createModel("314 - SURREY CENTRAL/SUNBURY", buses);
        busList[94] = createModel("316 - SURREY CENTRAL STN/SCOTTSDALE", buses);
        busList[95] = createModel("319 - SCOTT ROAD STN/NEWTON EXCHA", buses);
        busList[96] = createModel("320 - LANGLEY/FLEETWOOD/SURREY CTRL STN", buses);
        busList[97] = createModel("321 - WHITE ROCK/NEWTON/SURREY CTRL STN", buses);
        busList[98] = createModel("323 - NEWTON EXCH/SURREY CENTRAL STN", buses);
        busList[99] = createModel("324 - NEWTON EXCH/SURREY CENTRAL STN", buses);
        busList[100] = createModel("325 - NEWTON EXCH/SURREY CENTRAL STN", buses);
        busList[101] = createModel("326 - GUILDFORD/SURREY CENTRAL STN", buses);
        busList[102] = createModel("329 - SURREY CENTRAL STN/SCOTTSDALE", buses);
        busList[103] = createModel("335 - NEWTON/SURREY CENTRAL STN", buses);
        busList[104] = createModel("337 - FRASER HEIGHTS/GUILDFORD/SURREY CTR", buses);
        busList[105] = createModel("340 - SCOTTSDALE/22ND ST STN", buses);
        busList[106] = createModel("341 - GUILDFORD/NEWTON EXCH", buses);
        busList[107] = createModel("342 - LANGLEY CENTRE/NEWTON EXCHANGE", buses);
        busList[108] = createModel("345 - KING GEORGE STN/WHITE ROCK CENTRE", buses);
        busList[109] = createModel("351 - CRESCENT BEACH/BRIDGEPORT STN", buses);
        busList[110] = createModel("352 - OCEAN PARK /BRIDGEPORT STN", buses);
        busList[111] = createModel("354 - WHITE ROCK SOUTH/BRIDGEPORT STATION", buses);
        busList[112] = createModel("364 - LANGLEY CTR/ SCOTTSDALE EX", buses);
        busList[113] = createModel("375 - WHITE ROCK/WHITE ROCK STH/GUILDFORD", buses);
        busList[114] = createModel("388 - 22ST STN/CARVOLTH EXCH", buses);
        busList[115] = createModel("391 - SCOTTSDALE/SCOTT ROAD STN", buses);
        busList[116] = createModel("393 - NEWTON EXCH/SURREY CENTRAL STN", buses);
        busList[117] = createModel("394 - WHITE ROCK/KING GEORGE STN EXPRESS", buses);
        busList[118] = createModel("395 - LANGLEY CENTRE/KING GEORGE STN", buses);
        busList[119] = createModel("401 - ONE ROAD/GARDEN CITY", buses);
        busList[120] = createModel("402 - TWO ROAD/BRIGHOUSE STATION", buses);
        busList[121] = createModel("403 - BRIDGEPORT STN/THREE ROAD", buses);
        busList[122] = createModel("404 - FOUR ROAD/BRIGHOUSE STATION", buses);
        busList[123] = createModel("405 - FIVE ROAD/CAMBIE", buses);
        busList[124] = createModel("407 - GILBERT/BRIDGEPORT", buses);
        busList[125] = createModel("410 - 22ND ST STN/QUEENSBOROUGH/RAILWAY", buses);
        busList[126] = createModel("430 - METROTOWN/BRIGHOUSE STATION", buses);
        busList[127] = createModel("480 - UBC/BRIDGEPORT STN", buses);
        busList[128] = createModel("501 - LANGLEY CENTRE/SURREY CENTRAL STN", buses);
        busList[129] = createModel("502 - LANGLEY CENTRE/SURREY CENTRAL STN", buses);
        busList[130] = createModel("503 - ALDERGROVE/SURREY CENTRAL STN", buses);
        busList[131] = createModel("509 - WALNUT GROVE/SURREY CENTRAL STN", buses);
        busList[132] = createModel("531 - WHITE ROCK CENTRE/WILLOWBROOK", buses);
        busList[133] = createModel("555 - CARVOLTH EXCH / LOUGHEED STN", buses);
        busList[134] = createModel("590 - LANGLEY SOUTH/LANGLEY CENTRE", buses);
        busList[135] = createModel("595 - MAPLE MEADOWS STN/LANGLEY CENTRE", buses);
        busList[136] = createModel("601 - SOUTH DELTA/BOUNDARY BAY/BRIDGEPORT", buses);
        busList[137] = createModel("602 - TSAWWASSEN HEIGHTS/BRIDGEPORT", buses);
        busList[138] = createModel("603 - BEACH GROVE/BRIDGEPORT", buses);
        busList[139] = createModel("604 - ENGLISH BLUFF/BRIDGEPORT", buses);
        busList[140] = createModel("606 - LADNER RING", buses);
        busList[141] = createModel("608 - LADNER RING", buses);
        busList[142] = createModel("609 - TSAWWASSEN FIRST NATION/SOUTH DELTA", buses);
        busList[143] = createModel("620 - TSAWWASSEN FERRY/BRIDGEPORT STATION", buses);
        busList[144] = createModel("640 - LADNER EXCH/SCOTT ROAD STN", buses);
        busList[145] = createModel("701 - HANEY/MAPLE RIDGE EAST/COQ STN", buses);
        busList[146] = createModel("791 - HANEY PLACE/BRAID STN", buses);
        busList[147] = createModel("804 - HOLY CROSS SCHOOL", buses);
        busList[148] = createModel("807 - SCHOOL SPECIAL", buses);
        busList[149] = createModel("828 - KWANTLEN PARK SCHOOL", buses);
        busList[150] = createModel("840 - BROOKSWOOD SCHOOL", buses);
        busList[151] = createModel("848 - PORT MOODY SS", buses);
        busList[152] = createModel("855 - ELGIN PARK SCHOOL SPECIAL", buses);
        busList[153] = createModel("861 - RIVERSIDE SCHOOL", buses);
        busList[154] = createModel("863 - TERRY FOX/ARCH CARNEY", buses);
        busList[155] = createModel("864 - LORD TWEEDSMUIR SCHOOL", buses);
        busList[156] = createModel("865 - ROBERTSON", buses);
        busList[157] = createModel("867 - HERITAGE WOODS SCHOOL", buses);
        busList[158] = createModel("880 - WINDSOR SCHOOL SPECIALS", buses);
        busList[159] = createModel("881 - CARSON GRAHAM SCHOOL SPECIALS", buses);
        busList[160] = createModel("C1 - HASTINGS & GILMORE/KOOTENAY LOOP", buses);
        busList[161] = createModel("C2 - CAPITOL HILL/HASTINGS AT GILMORE", buses);
        busList[162] = createModel("C3 - QUAYSIDE/VICTORIA HILL", buses);
        busList[163] = createModel("C4 - UPTOWN/NEW WESTMINSTER STATION", buses);
        busList[164] = createModel("C5 - ROYAL OAK STN/EDMONDS STN", buses);
        busList[165] = createModel("C6 - METROTOWN STN/SUNCREST", buses);
        busList[166] = createModel("C7 - METROTOWN STN/EDMONDS STN", buses);
        busList[167] = createModel("C9 - NEW WESTMINSTER STN/LOUGHEED STN", buses);
        busList[168] = createModel("C10 - BLUEWATER/SNUG COVE", buses);
        busList[169] = createModel("C11 - EAGLE CLIFF/SNUG COVE", buses);
        busList[170] = createModel("C12 - LIONS BAY/CAULFEILD", buses);
        busList[171] = createModel("C15 - INDIAN RIVER/PHIBBS EXCH", buses);
        busList[172] = createModel("C18 - WEST MALL/UBC LOOP", buses);
        busList[173] = createModel("C19 - ALMA/SPANISH BANKS", buses);
        busList[174] = createModel("C20 - TOTEM PARK/UBC LOOP", buses);
        busList[175] = createModel("C21 - YALETOWN/BEACH", buses);
        busList[176] = createModel("C23 - YALETOWN/DAVIE/MAIN ST STN", buses);
        busList[177] = createModel("C24 - PORT MOODY STN/LOUGHEED STN", buses);
        busList[178] = createModel("C25 - IOCO/PORT MOODY STN", buses);
        busList[179] = createModel("C26 - BELCARRA/PORT MOODY STN", buses);
        busList[180] = createModel("C27 - COQUITLAM STN/PORT MOODY STN", buses);
        busList[181] = createModel("C28 - COQUITLAM STN/PORT MOODY STN", buses);
        busList[182] = createModel("C29 - COQUITLAM STN/PARKWAY BOULEVARD", buses);
        busList[183] = createModel("C30 - LAFARGE PARK/COQUITLAM STN", buses);
        busList[184] = createModel("C36 - PORT COQ STN/PORT COQ SOUTH", buses);
        busList[185] = createModel("C37 - PORT COQ STN/PRAIRIE/RIVERSIDE", buses);
        busList[186] = createModel("C38 - COQ STN/RIVER SPR/PRAIRIE/POCO", buses);
        busList[187] = createModel("C40 - MERIDIAN/PORT COQUITLAM STN", buses);
        busList[188] = createModel("C41 - MEADOWTOWN/MAPLE MDWS STN/P MDW CTR", buses);
        busList[189] = createModel("C43 - HANEY PL/MAPLE MDWS STN/MEADOWTOWN", buses);
        busList[190] = createModel("C44 - HANEY PL/MAPLE MDWS STN/MEADOWTOWN", buses);
        busList[191] = createModel("C45 - COTTONWOOD/HANEY PLACE", buses);
        busList[192] = createModel("C46 - ALBION/HANEY PLACE", buses);
        busList[193] = createModel("C47 - ALOUETTE/HANEY PLACE", buses);
        busList[194] = createModel("C48 - THORNHILL/HANEY PLACE", buses);
        busList[195] = createModel("C49 - RUSKIN/HANEY PLACE", buses);
        busList[196] = createModel("C50 - OCEAN PARK/PEACE ARCH HOSPITAL", buses);
        busList[197] = createModel("C51 - OCEAN PARK/WHITE ROCK CENTRE", buses);
        busList[198] = createModel("C52 - SEASIDE/WHITE ROCK CENTRE", buses);
        busList[199] = createModel("C53 - CRANLEY DRIVE/WHITE ROCK CENTRE", buses);
        busList[200] = createModel("C60 - LANGLEY CENTRE/LANGLEY HOSPITAL", buses);
        busList[201] = createModel("C61 - LANGLEY CENTRE/BROOKSWOOD", buses);
        busList[202] = createModel("C62 - LANGLEY CENTRE/WALNUT GROVE", buses);
        busList[203] = createModel("C63 - LANGLEY CENTRE/FERNRIDGE", buses);
        busList[204] = createModel("C64 - LANGLEY CTR/WILLOWBROOK", buses);
        busList[205] = createModel("C70 - CLOVERDALE/WILLOWBROOK", buses);
        busList[206] = createModel("C71 - SURREY CNTRL/SCOTT RD STN", buses);
        busList[207] = createModel("C73 - GUILDFORD/ SURREY CNTRL STN", buses);
        busList[208] = createModel("C75 - NEWTON EX/SCOTTSDALE", buses);
        busList[209] = createModel("C76 - SCOTTSDALE/LADNER EX", buses);
        busList[210] = createModel("C84 - ENGLISH BLUFF/SOUTH DELTA REC CENTR", buses);
        busList[211] = createModel("C86 - LADNER SOUTH/LADNER EXCH", buses);
        busList[212] = createModel("C87 - EAST LADNER/LADNER EXCH", buses);
        busList[213] = createModel("C88 - LADNER NORTH/LADNER EXCH", buses);
        busList[214] = createModel("C89 - BOUNDARY BAY/SOUTH DELTA REC CENTRE", buses);
        busList[215] = createModel("C92 - BRIDGEPORT STATION/SEA ISLAND SOUTH", buses);
        busList[216] = createModel("C93 - RIVERPORT/STEVESTON", buses);
        busList[217] = createModel("C94 - RICHMOND OVAL/BRIGHOUSE STN", buses);
        busList[218] = createModel("C96 - EAST CAMBIE/BRIGHOUSE STATION", buses);
        busList[219] = createModel("C98 - KINGSWOOD/22ND ST STN", buses);
        busList[220] = createModel("N8 - DOWNTOWN/FRASER NIGHTBUS", buses);
        busList[221] = createModel("N9 - DOWNTOWN/COQUITLAM STN NIGHTBUS", buses);
        busList[222] = createModel("N10 - DOWNTOWN/RICHMOND NIGHTBUS", buses);
        busList[223] = createModel("N15 - DOWNTOWN/CAMBIE NIGHTBUS", buses);
        busList[224] = createModel("N17 - DOWNTOWN/UBC NIGHTBUS", buses);
        busList[225] = createModel("N19 - DOWNTOWN/SURREY CNTRL STN NIGHTBUS", buses);
        busList[226] = createModel("N20 - DOWNTOWN/VICTORIA NIGHTBUS", buses);
        busList[227] = createModel("N22 - DOWNTOWN/MACDONALD NIGHTBUS", buses);
        busList[228] = createModel("N24 - DOWNTOWN/ LYNN VALLEY NIGHTBUS", buses);
        busList[229] = createModel("N35 - DOWNTOWN/SFU NIGHTBUS", buses);
        return busList;
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
