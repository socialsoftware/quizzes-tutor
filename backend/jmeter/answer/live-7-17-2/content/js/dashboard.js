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

    var data = {"OkPercent": 97.46330340601396, "KoPercent": 2.5366965939860338};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.006983041185691891, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.011666666666666667, 500, 1500, "Start quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.03666666666666667, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.035, 500, 1500, "Login as student"], "isController": false}, {"data": [0.005128205128205128, 500, 1500, "Conclude quiz"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 7017, 178, 2.5366965939860338, 8427.41157189684, 87, 130489, 11668.2, 13205.3, 17561.499999999993, 34.3920011762976, 21.419175105989314, 28.07993635280351], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 297.0, 297, 297, 297.0, 297.0, 297.0, 3.3670033670033668, 11.623395412457914, 2.6271832912457915], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 311.0, 311, 311, 311.0, 311.0, 311.0, 3.215434083601286, 4.286198754019293, 0.48671121382636656], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 111.80000000000001, 87, 172, 163.9000000000001, 171.85, 172.0, 8.861320336730172, 7.226649036331413, 10.62752686087727], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 9934.940000000002, 970, 25276, 14365.800000000001, 17016.899999999994, 22032.090000000004, 5.30194581411378, 6.943219059832459, 4.136967485817295], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 260.0, 260, 260, 260.0, 260.0, 260.0, 3.8461538461538463, 6.092247596153846, 5.95703125], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 7206.590000000004, 465, 24764, 12651.2, 16206.149999999994, 20030.32, 6.540222367560497, 6.075785678275562, 5.186191955526488], "isController": false}, {"data": ["Submit answer", 5899, 105, 1.7799627055433125, 8602.139684692278, 147, 130489, 11531.0, 12896.0, 15989.0, 29.67512802712465, 14.73756738559305, 25.604162382851406], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 7079.326666666667, 343, 36166, 15560.800000000001, 21344.699999999975, 30514.030000000006, 8.095199546668825, 11.522088300075556, 1.225347587630535], "isController": false}, {"data": ["Conclude quiz", 195, 73, 37.43589743589744, 5752.635897435894, 245, 13017, 10081.0, 10825.199999999993, 12704.999999999998, 5.180521240137084, 8.48253276015515, 2.5384761629606016], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to quizzes-tutor.tecnico.ulisboa.pt:443 [quizzes-tutor.tecnico.ulisboa.pt\\\/192.92.147.23] failed: Connection timed out (Connection timed out)", 1, 0.5617977528089888, 0.014251104460595696], "isController": false}, {"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 2, 1.1235955056179776, 0.02850220892119139], "isController": false}, {"data": ["Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 174, 97.75280898876404, 2.4796921761436512], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.ConnectionClosedException\/Non HTTP response message: Connection is closed", 1, 0.5617977528089888, 0.014251104460595696], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 7017, 178, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 174, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 2, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to quizzes-tutor.tecnico.ulisboa.pt:443 [quizzes-tutor.tecnico.ulisboa.pt\\\/192.92.147.23] failed: Connection timed out (Connection timed out)", 1, "Non HTTP response code: org.apache.http.ConnectionClosedException\/Non HTTP response message: Connection is closed", 1, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Submit answer", 5899, 105, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 102, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Socket closed", 2, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to quizzes-tutor.tecnico.ulisboa.pt:443 [quizzes-tutor.tecnico.ulisboa.pt\\\/192.92.147.23] failed: Connection timed out (Connection timed out)", 1, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": ["Conclude quiz", 195, 73, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 72, "Non HTTP response code: org.apache.http.ConnectionClosedException\/Non HTTP response message: Connection is closed", 1, null, null, null, null, null, null], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
