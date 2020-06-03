/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 6;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "KO",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "OK",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.062188418765179596, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "Submit answer 16"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 15"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 14"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 13"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 12"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 11"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 10"], "isController": false}, {"data": [0.025, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.35, 500, 1500, "Login as student"], "isController": false}, {"data": [0.145, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 9"], "isController": false}, {"data": [0.0, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.023333333333333334, 500, 1500, "Submit answer 20"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0016666666666666668, 500, 1500, "Submit answer 19"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 18"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 17"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 7823, 0, 0.0, 4578.6529464400255, 0, 10189, 6319.0, 6840.799999999999, 7847.76, 62.18205519521811, 50.88424828160768, 47.358049443397874], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Submit answer 16", 300, 0, 0.0, 5052.276666666667, 2790, 8024, 6373.9, 6679.2, 7564.250000000001, 9.860960457548565, 3.7748989251553104, 8.156478034710581], "isController": false}, {"data": ["Submit answer 15", 300, 0, 0.0, 5046.880000000006, 2947, 8960, 6406.9000000000015, 6893.8, 8111.380000000003, 9.85545335085414, 3.77279073587385, 8.15192284001314], "isController": false}, {"data": ["Submit answer 14", 300, 0, 0.0, 5072.906666666661, 3078, 8507, 6439.300000000001, 6942.45, 7645.540000000001, 10.15744032503809, 3.888395124428644, 8.401710893854748], "isController": false}, {"data": ["Submit answer 13", 300, 0, 0.0, 5063.010000000001, 2935, 10012, 6323.3, 6999.65, 8637.380000000001, 10.48731035447109, 4.014673495070964, 8.674562373278333], "isController": false}, {"data": ["Submit answer 12", 300, 0, 0.0, 4951.293333333335, 3055, 8316, 6308.800000000001, 6973.0, 8026.99, 11.063170704723975, 4.235120035402146, 9.140080484566877], "isController": false}, {"data": ["Submit answer 11", 300, 0, 0.0, 4951.976666666668, 2930, 9327, 6170.000000000001, 6872.55, 8062.070000000001, 11.028600838173663, 4.221886258363355, 9.122289951106536], "isController": false}, {"data": ["Submit answer 10", 300, 0, 0.0, 5028.236666666673, 2904, 10189, 6214.6, 6872.149999999999, 8216.950000000003, 10.633395952220608, 4.070596887959451, 8.785012671463509], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 3198.1433333333325, 324, 7627, 4405.9, 4965.15, 6370.880000000001, 27.387255796969143, 80.34308243563994, 21.10209455450064], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 1431.3499999999995, 127, 4709, 2532.000000000001, 2769.1499999999987, 4125.690000000001, 41.52823920265781, 52.03196376661129, 5.393804505813954], "isController": false}, {"data": ["Conclude quiz", 300, 0, 0.0, 3027.69, 95, 7508, 5224.500000000003, 5593.0, 6922.59, 13.37196344996657, 5.811058335190551, 10.172616726097615], "isController": false}, {"data": ["Submit answer 2", 300, 0, 0.0, 5302.840000000004, 2995, 9850, 6679.400000000001, 7356.65, 8816.950000000008, 11.947431302270013, 4.573626045400239, 9.858964307048984], "isController": false}, {"data": ["Submit answer 3", 300, 0, 0.0, 5245.439999999999, 2680, 9253, 6590.1, 7018.099999999999, 8344.290000000003, 12.424418123084568, 4.756222562743312, 10.252571595709433], "isController": false}, {"data": ["Submit answer 4", 300, 0, 0.0, 5194.373333333331, 2899, 8804, 6372.300000000001, 6869.45, 8064.660000000002, 12.079726192872961, 4.62427018320918, 9.979930038252466], "isController": false}, {"data": ["Submit answer 5", 300, 0, 0.0, 5135.386666666665, 2936, 8489, 6472.8, 6993.95, 7714.280000000001, 12.459506603538498, 4.769654871667082, 10.293693932220284], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 65.0, 65, 65, 65.0, 65.0, 65.0, 15.384615384615385, 19.275841346153847, 1.9981971153846154], "isController": false}, {"data": ["Submit answer 6", 300, 0, 0.0, 5151.54, 3141, 8699, 6280.400000000001, 6639.849999999999, 7942.050000000001, 11.951238945103976, 4.575083658672615, 9.873777487849573], "isController": false}, {"data": ["Submit answer 7", 300, 0, 0.0, 5398.626666666671, 3241, 9269, 6738.5, 7267.299999999999, 8606.560000000001, 10.767353384538081, 4.121877467518484, 8.895684534491423], "isController": false}, {"data": ["Submit answer 8", 300, 0, 0.0, 5236.536666666666, 3018, 8661, 6557.400000000001, 7038.15, 8024.550000000003, 10.866809142608759, 4.159950374904915, 8.97785208461622], "isController": false}, {"data": ["Submit answer 9", 300, 0, 0.0, 5055.156666666667, 3052, 8042, 6289.1, 6765.599999999999, 7619.220000000003, 10.686044026501389, 4.090751228895063, 8.828509029707202], "isController": false}, {"data": ["Submit null answer 4", 300, 0, 0.0, 5179.790000000001, 2869, 9453, 6540.8, 6841.3, 8303.550000000003, 11.995201919232308, 4.591913234706118, 9.886670331867252], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 114.0, 114, 114, 114.0, 114.0, 114.0, 8.771929824561402, 106.47101151315789, 13.551946271929824], "isController": false}, {"data": ["Submit answer 1", 300, 0, 0.0, 5254.086666666662, 3015, 8789, 6697.700000000001, 7109.15, 8094.180000000004, 15.077649896969394, 5.771912851183596, 12.45673028597276], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 152.0, 152, 152, 152.0, 152.0, 152.0, 6.578947368421052, 721.2877775493421, 4.992033305921053], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 4796.290000000004, 2745, 8168, 6081.300000000001, 6447.9, 7875.250000000003, 18.196154546005946, 116.17439118699582, 13.78927336689513], "isController": false}, {"data": ["Submit answer 20", 300, 0, 0.0, 4262.783333333331, 649, 7699, 5789.200000000001, 6463.599999999999, 7557.72, 11.497336450388994, 4.401324109914537, 9.510003880351052], "isController": false}, {"data": ["Debug Sampler", 300, 0, 0.0, 0.19666666666666677, 0, 4, 1.0, 1.0, 1.0, 16.732667744994142, 30.418813444698536, 0.0], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 87.0, 71, 111, 105.30000000000001, 110.75, 111.0, 11.435105774728417, 11.33571862492853, 13.468633862206975], "isController": false}, {"data": ["Submit answer 19", 300, 0, 0.0, 4982.443333333333, 1373, 8890, 6451.6, 7025.15, 7766.620000000001, 10.27678816114004, 3.934082967936421, 8.500429270005482], "isController": false}, {"data": ["Submit answer 18", 300, 0, 0.0, 5179.723333333334, 2802, 8978, 6543.8, 7137.299999999999, 8131.610000000002, 9.392905225586274, 3.5957215316697453, 7.769326880929271], "isController": false}, {"data": ["Submit answer 17", 300, 0, 0.0, 5190.126666666663, 2130, 8481, 6637.600000000001, 7097.599999999999, 8142.490000000003, 9.051138934982653, 3.4648891235480463, 7.486635427666314], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Percentile 1
            case 8:
            // Percentile 2
            case 9:
            // Percentile 3
            case 10:
            // Throughput
            case 11:
            // Kbytes/s
            case 12:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 7823, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
