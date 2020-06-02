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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.09993857022191507, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.079, 500, 1500, "Submit answer 16"], "isController": false}, {"data": [0.046, 500, 1500, "Submit answer 15"], "isController": false}, {"data": [0.039, 500, 1500, "Submit answer 14"], "isController": false}, {"data": [0.018, 500, 1500, "Submit answer 13"], "isController": false}, {"data": [0.024, 500, 1500, "Submit answer 12"], "isController": false}, {"data": [0.023, 500, 1500, "Submit answer 11"], "isController": false}, {"data": [0.019, 500, 1500, "Submit answer 10"], "isController": false}, {"data": [0.069, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.364, 500, 1500, "Login as student"], "isController": false}, {"data": [0.379, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.003, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.009, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.016, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.018, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.012, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.015, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [0.012, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.014, 500, 1500, "Submit answer 9"], "isController": false}, {"data": [0.019, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.003, 500, 1500, "Submit answer 1"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.003, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.164, 500, 1500, "Submit answer 20"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.072, 500, 1500, "Submit answer 19"], "isController": false}, {"data": [0.073, 500, 1500, "Submit answer 18"], "isController": false}, {"data": [0.064, 500, 1500, "Submit answer 17"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 13023, 0, 0.0, 1919.613990631972, 0, 4173, 2569.0, 2755.0, 3196.0, 243.99063231850118, 182.91722775175643, 185.71820111241217], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Submit answer 16", 500, 0, 0.0, 1802.3080000000004, 316, 2996, 2222.0, 2354.85, 2664.170000000001, 38.47041624990383, 14.726956220666308, 31.82074469108256], "isController": false}, {"data": ["Submit answer 15", 500, 0, 0.0, 1856.7119999999986, 426, 2770, 2215.7000000000003, 2372.5499999999997, 2589.9700000000003, 43.43671270958214, 16.628116584136915, 35.92860904786726], "isController": false}, {"data": ["Submit answer 14", 500, 0, 0.0, 1906.6159999999993, 381, 3358, 2309.9000000000005, 2443.3499999999995, 2844.3700000000017, 42.90372404324695, 16.424081860305474, 35.44585013729192], "isController": false}, {"data": ["Submit answer 13", 500, 0, 0.0, 1956.4060000000013, 675, 2995, 2381.4, 2518.85, 2834.3900000000003, 41.342814618819254, 15.826546221266744, 34.1966445138085], "isController": false}, {"data": ["Submit answer 12", 500, 0, 0.0, 1980.9479999999996, 458, 3117, 2393.8, 2589.5499999999997, 2928.6100000000006, 41.83400267737617, 16.014579149933066, 34.60292994896252], "isController": false}, {"data": ["Submit answer 11", 500, 0, 0.0, 2028.9760000000024, 335, 3860, 2422.8, 2577.95, 2951.4500000000016, 44.255620463798905, 16.941604708798017, 36.605967317224284], "isController": false}, {"data": ["Submit answer 10", 500, 0, 0.0, 2089.0139999999983, 451, 3596, 2522.8, 2684.2999999999997, 2954.86, 45.08566275924257, 17.259355275022543, 37.2485065374211], "isController": false}, {"data": ["Get quizzes available", 500, 0, 0.0, 2209.6280000000006, 414, 3533, 2954.5, 3137.7, 3453.98, 63.275120222728425, 94.41834345735256, 48.75397446848899], "isController": false}, {"data": ["Login as student", 500, 0, 0.0, 1304.020000000001, 78, 3238, 2050.8, 2297.85, 2857.020000000002, 95.63886763580719, 119.64198187643458, 12.421845112853864], "isController": false}, {"data": ["Conclude quiz", 500, 0, 0.0, 1242.8960000000002, 32, 2420, 1917.8000000000002, 2053.8, 2245.8500000000004, 64.28387760349705, 27.93586477886346, 48.9034576690666], "isController": false}, {"data": ["Submit answer 2", 500, 0, 0.0, 2296.055999999998, 672, 3755, 2804.3, 2996.65, 3381.6400000000003, 52.38893545683151, 20.055139354568315, 43.231103965842415], "isController": false}, {"data": ["Submit answer 3", 500, 0, 0.0, 2178.597999999998, 504, 3388, 2567.9, 2748.95, 3003.88, 53.00540655146825, 20.29113219548394, 43.73981302342839], "isController": false}, {"data": ["Submit answer 4", 500, 0, 0.0, 2110.3460000000027, 292, 3672, 2545.4, 2670.7, 2930.84, 51.711655807218946, 19.795868238701, 42.722715637604715], "isController": false}, {"data": ["Submit answer 5", 500, 0, 0.0, 2168.9239999999977, 422, 3234, 2658.5, 2832.8, 3074.79, 51.594262717985764, 19.750928696728923, 42.62572876896089], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 243.0, 243, 243, 243.0, 243.0, 243.0, 4.11522633744856, 5.148051697530864, 0.5344971707818931], "isController": false}, {"data": ["Submit answer 6", 500, 0, 0.0, 2150.1299999999983, 298, 3256, 2551.5, 2698.9, 2966.8900000000003, 43.45558838866678, 16.635342430036502, 35.90178493829306], "isController": false}, {"data": ["Submit answer 7", 500, 0, 0.0, 2089.7439999999997, 444, 4015, 2522.8, 2688.0, 3070.7700000000013, 42.444821731748725, 16.24840831918506, 35.06671795415959], "isController": false}, {"data": ["Submit answer 8", 500, 0, 0.0, 2140.822000000001, 553, 3137, 2557.4, 2712.8, 2982.84, 42.59669449650707, 16.306547111944113, 35.19219096098143], "isController": false}, {"data": ["Submit answer 9", 500, 0, 0.0, 2121.4399999999996, 265, 3495, 2598.9, 2739.3999999999996, 3119.210000000002, 42.50255015300918, 16.270507480448828, 35.11441155219313], "isController": false}, {"data": ["Submit null answer 4", 500, 0, 0.0, 2104.9919999999997, 284, 3408, 2590.1000000000004, 2716.7999999999997, 3019.960000000001, 53.24246619103397, 20.381881588755192, 43.88343893089128], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 84.0, 84, 84, 84.0, 84.0, 84.0, 11.904761904761903, 144.4731212797619, 18.391927083333332], "isController": false}, {"data": ["Submit answer 1", 500, 0, 0.0, 2515.984000000002, 1262, 4173, 3016.5, 3189.8999999999996, 3553.4600000000005, 49.304802287742824, 18.87449462577655, 40.73424095256878], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 45.0, 45, 45, 45.0, 45.0, 45.0, 22.22222222222222, 451.4322916666667, 16.861979166666668], "isController": false}, {"data": ["Start quiz", 500, 0, 0.0, 2656.1539999999995, 833, 4023, 3222.0, 3414.1499999999996, 3723.5800000000004, 51.39274334463974, 328.1198808009559, 38.9460633158598], "isController": false}, {"data": ["Submit answer 20", 500, 0, 0.0, 1659.1440000000005, 136, 2803, 2139.8, 2295.0, 2584.8500000000004, 53.146258503401356, 20.345052083333336, 43.959844680059526], "isController": false}, {"data": ["Debug Sampler", 500, 0, 0.0, 0.12799999999999975, 0, 2, 1.0, 1.0, 1.0, 87.56567425569177, 159.19490887697023, 0.0], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 34.3, 23, 155, 39.7, 149.24999999999991, 155.0, 28.77697841726619, 28.526866007194247, 33.894446942446045], "isController": false}, {"data": ["Submit answer 19", 500, 0, 0.0, 1813.3039999999983, 203, 3021, 2241.7000000000003, 2446.85, 2758.7400000000002, 44.60701222232135, 17.076121866357393, 36.89662046123651], "isController": false}, {"data": ["Submit answer 18", 500, 0, 0.0, 1803.768, 225, 2926, 2236.9, 2361.9, 2565.87, 40.64710186163727, 15.560218681408015, 33.62118679375661], "isController": false}, {"data": ["Submit answer 17", 500, 0, 0.0, 1809.0919999999996, 366, 2706, 2189.8, 2264.95, 2464.99, 38.028597505324, 14.557822482506845, 31.455295006845148], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 13023, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
