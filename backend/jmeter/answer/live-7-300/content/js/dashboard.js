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

    var data = {"OkPercent": 89.68363136176066, "KoPercent": 10.31636863823934};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.01186382393397524, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.011666666666666667, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.028333333333333332, 500, 1500, "Login as student"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.0, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2908, 300, 10.31636863823934, 20586.90990371394, 45, 60092, 31319.3, 35077.45, 42415.169999999955, 14.095635103366375, 13.725809481108067, 9.556684505344029], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 498.0, 498, 498, 498.0, 498.0, 498.0, 2.008032128514056, 8.80867218875502, 1.5668141315261044], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 20935.180000000008, 3118, 42891, 28986.400000000023, 32364.199999999993, 39578.22, 3.1329302296437858, 4.098835463725889, 2.4414827375544346], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 13590.920000000004, 542, 36131, 20495.9, 23007.449999999997, 30528.330000000024, 5.2567944067707515, 4.132316116236486, 4.163340101456132], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 8115.423333333331, 628, 21422, 14990.7, 17814.85, 20630.110000000008, 13.892104653855059, 18.545417052558463, 2.1028088099097015], "isController": false}, {"data": ["Submit answer 2", 300, 0, 0.0, 27081.136666666647, 9403, 52455, 35482.600000000006, 39646.95, 51700.02000000004, 2.3920965131206495, 1.0605584149968503, 2.025339528198831], "isController": false}, {"data": ["Submit answer 3", 300, 0, 0.0, 25454.706666666665, 8973, 46203, 34195.0, 35483.299999999996, 39373.87, 2.5849812588858727, 1.146075675326354, 2.1911755202274783], "isController": false}, {"data": ["Submit answer 4", 300, 1, 0.3333333333333333, 26399.220000000012, 12805, 60092, 35343.400000000016, 40132.849999999984, 45999.920000000006, 2.305989423195179, 1.0241760771853092, 1.9546863470052884], "isController": false}, {"data": ["Submit answer 5", 279, 119, 42.65232974910394, 19567.706093189972, 489, 44784, 28685.0, 29536.0, 33932.79999999997, 3.5048930316696607, 6.060581004327727, 1.7037674459505296], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 530.0, 530, 530, 530.0, 530.0, 530.0, 1.8867924528301887, 2.518794221698113, 0.28559846698113206], "isController": false}, {"data": ["Submit answer 6", 160, 119, 74.375, 15308.075, 45, 44556, 25742.2, 28917.9, 39211.78999999988, 2.6534436723660426, 7.109557683377834, 0.5763595789316572], "isController": false}, {"data": ["Submit answer 7", 41, 35, 85.36585365853658, 11456.463414634145, 402, 33034, 24288.400000000012, 28823.899999999998, 33034.0, 1.1515883492964076, 3.433809124231104, 0.14285137488413896], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 232.79999999999998, 197, 295, 281.40000000000003, 294.45, 295.0, 4.2753313381787095, 3.4874846355280034, 5.127475016032492], "isController": false}, {"data": ["Submit answer 8", 6, 6, 100.0, 5381.333333333334, 989, 9953, 9953.0, 9953.0, 9953.0, 0.5908419497784342, 2.0431360782865586, 0.0], "isController": false}, {"data": ["Submit null answer 4", 299, 20, 6.688963210702341, 24709.367892976577, 12703, 46105, 32911.0, 36423.0, 41155.0, 2.7734490946868506, 1.7888971307787918, 2.1886239599565895], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 338.0, 338, 338, 338.0, 338.0, 338.0, 2.9585798816568047, 4.721015162721893, 4.634338017751479], "isController": false}, {"data": ["Submit answer 1", 300, 0, 0.0, 25296.5, 9191, 52238, 34006.5, 37874.6, 46453.48000000001, 2.4330702914007185, 1.0787245237264904, 2.0624072391951405], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["504\/Gateway Time-out", 1, 0.3333333333333333, 0.0343878954607978], "isController": false}, {"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 1, 0.3333333333333333, 0.0343878954607978], "isController": false}, {"data": ["Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 297, 99.0, 10.213204951856946], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.NoHttpResponseException\/Non HTTP response message: The target server failed to respond", 1, 0.3333333333333333, 0.0343878954607978], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2908, 300, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 297, "504\/Gateway Time-out", 1, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 1, "Non HTTP response code: org.apache.http.NoHttpResponseException\/Non HTTP response message: The target server failed to respond", 1, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Submit answer 4", 300, 1, "504\/Gateway Time-out", 1, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["Submit answer 5", 279, 119, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 119, null, null, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": ["Submit answer 6", 160, 119, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 118, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 1, null, null, null, null, null, null], "isController": false}, {"data": ["Submit answer 7", 41, 35, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 34, "Non HTTP response code: org.apache.http.NoHttpResponseException\/Non HTTP response message: The target server failed to respond", 1, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": ["Submit answer 8", 6, 6, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 6, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["Submit null answer 4", 299, 20, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 20, null, null, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
