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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.0428863607311773, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "Submit answer 16"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 15"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 14"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 13"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 12"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 11"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 10"], "isController": false}, {"data": [0.0016666666666666668, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.025, 500, 1500, "Login as student"], "isController": false}, {"data": [0.015, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 9"], "isController": false}, {"data": [0.0, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.0016666666666666668, 500, 1500, "Submit answer 20"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 19"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 18"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 17"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 7823, 0, 0.0, 19973.373002684482, 0, 49143, 28494.200000000004, 30498.8, 36530.56000000002, 14.444986077566787, 9.147403372150887, 11.295459675721787], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Submit answer 16", 300, 0, 0.0, 22656.000000000007, 11168, 37256, 29681.90000000001, 33395.5, 37031.88, 1.7200749952697938, 0.7626113748559437, 1.4597120809467292], "isController": false}, {"data": ["Submit answer 15", 300, 0, 0.0, 22657.766666666663, 8634, 41755, 30012.500000000004, 32560.449999999983, 37266.270000000004, 1.8280644453652473, 0.8104895099568576, 1.5495702525166353], "isController": false}, {"data": ["Submit answer 14", 300, 0, 0.0, 22313.993333333325, 7938, 40891, 29684.100000000002, 32489.35, 37673.07, 1.9278471088719524, 0.8547290892850258, 1.634151650879741], "isController": false}, {"data": ["Submit answer 13", 300, 0, 0.0, 22251.996666666662, 10684, 40713, 29139.0, 30247.149999999998, 35897.16, 1.9249401664431598, 0.8534402691066353, 1.6316875629615848], "isController": false}, {"data": ["Submit answer 12", 300, 0, 0.0, 21850.46333333333, 7733, 39942, 28253.800000000025, 30213.399999999998, 33737.17, 2.0801264716894785, 0.9222435724092024, 1.7632322045180346], "isController": false}, {"data": ["Submit answer 11", 300, 0, 0.0, 22524.63, 9510, 42527, 29895.600000000002, 31697.999999999985, 37405.990000000005, 2.0848970060878993, 0.9243586335585022, 1.767275977816696], "isController": false}, {"data": ["Submit answer 10", 300, 0, 0.0, 22722.873333333333, 10944, 43766, 29561.1, 33011.999999999985, 39956.93, 2.0079246091240095, 0.8902321997483402, 1.7020298444527737], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 14587.953333333331, 1332, 32468, 19989.4, 21383.9, 29998.64000000002, 6.535520554212143, 5.130213439208767, 5.176081220181688], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 8928.963333333339, 391, 23161, 14353.300000000001, 16196.099999999999, 20454.79000000003, 12.683380543694245, 16.931817581279333, 1.919847640891219], "isController": false}, {"data": ["Conclude quiz", 300, 0, 0.0, 13176.933333333329, 352, 35898, 22504.9, 25585.05, 33421.780000000006, 2.8165311602230694, 1.5815482589143213, 2.2031654876354283], "isController": false}, {"data": ["Submit answer 2", 300, 0, 0.0, 23488.646666666682, 11576, 43303, 30538.3, 32052.05, 35488.92, 3.269398430688753, 1.4495184448561464, 2.7649404697035744], "isController": false}, {"data": ["Submit answer 3", 300, 0, 0.0, 23505.420000000006, 11285, 43384, 30494.300000000003, 34007.399999999994, 38989.93000000001, 2.8995312424491373, 1.2855343594452229, 2.4578057797322765], "isController": false}, {"data": ["Submit answer 4", 300, 0, 0.0, 22963.573333333345, 11299, 49143, 30547.600000000002, 31020.85, 37529.48000000001, 2.852226162519847, 1.2645612087734477, 2.417707333073464], "isController": false}, {"data": ["Submit answer 5", 300, 0, 0.0, 22393.369999999974, 7661, 44821, 29531.800000000003, 32637.799999999992, 40672.54000000003, 2.6798395669379262, 1.1881319954978695, 2.2715827579122263], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 764.0, 764, 764, 764.0, 764.0, 764.0, 1.3089005235602096, 1.747331070026178, 0.1981245909685864], "isController": false}, {"data": ["Submit answer 6", 300, 0, 0.0, 22797.62666666667, 10784, 41152, 29808.9, 33028.39999999999, 40048.28000000002, 2.4014793112557338, 1.0647183665137725, 2.035628947431618], "isController": false}, {"data": ["Submit answer 7", 300, 0, 0.0, 22298.33333333332, 11752, 38126, 29943.300000000003, 30779.8, 37214.760000000024, 2.3465548663637, 1.0403670989542189, 1.9867803409544222], "isController": false}, {"data": ["Submit answer 8", 300, 0, 0.0, 22679.91, 9965, 41032, 29758.9, 31762.1, 34249.98, 2.2251891410769913, 0.9865584668446818, 1.8861954828660437], "isController": false}, {"data": ["Submit answer 9", 300, 0, 0.0, 22196.310000000023, 11171, 37710, 29536.9, 30574.85, 36765.67000000002, 2.068594596830913, 0.9171308075793305, 1.7514370268089858], "isController": false}, {"data": ["Submit null answer 4", 300, 0, 0.0, 22489.739999999994, 10404, 42836, 28317.100000000002, 31417.5, 39821.640000000014, 2.889978517826351, 1.2812990694269173, 2.444063863708613], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 356.0, 356, 356, 356.0, 356.0, 356.0, 2.8089887640449436, 9.430960323033709, 4.400017556179775], "isController": false}, {"data": ["Submit answer 1", 300, 0, 0.0, 21835.273333333345, 10174, 37294, 28254.600000000002, 30049.85, 35850.05000000002, 3.77126048096142, 1.6720236898012544, 3.1967325170649534], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 363.0, 363, 363, 363.0, 363.0, 363.0, 2.7548209366391188, 9.674156336088155, 2.149513601928375], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 19388.63999999998, 7274, 36677, 25521.300000000003, 28071.55, 35571.780000000035, 4.401602183194683, 11.457992323789194, 3.430154826356794], "isController": false}, {"data": ["Submit answer 20", 300, 0, 0.0, 17246.683333333327, 773, 33277, 25570.400000000005, 27548.64999999999, 31417.440000000002, 2.410219329959026, 1.0685933357435526, 2.043037478910581], "isController": false}, {"data": ["Debug Sampler", 300, 0, 0.0, 0.18, 0, 1, 1.0, 1.0, 1.0, 3.355554561317167, 6.162194137706367, 0.0], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 226.35, 184, 467, 256.9, 456.4999999999999, 467.0, 4.396570674873598, 3.584235546273906, 5.272879341613541], "isController": false}, {"data": ["Submit answer 19", 300, 0, 0.0, 20218.916666666657, 3642, 39309, 26713.7, 29425.3, 34050.35, 2.004865139404956, 0.8888757551658693, 1.6994364658237322], "isController": false}, {"data": ["Submit answer 18", 300, 0, 0.0, 21727.739999999994, 8080, 44132, 29435.800000000007, 30646.55, 40477.00000000003, 1.8221463669438354, 0.8078656744067395, 1.5463331961662041], "isController": false}, {"data": ["Submit answer 17", 300, 0, 0.0, 21917.019999999993, 8326, 36958, 29034.9, 29755.05, 34341.09000000001, 1.7220991240255787, 0.7635087913160282, 1.4614298230256135], "isController": false}]}, function(index, item){
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
