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

    var data = {"OkPercent": 63.55685131195335, "KoPercent": 36.44314868804665};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.021137026239067054, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of assessments"], "isController": false}, {"data": [0.5, 500, 1500, "Get list of questions"], "isController": false}, {"data": [1.0, 500, 1500, "Create assessment"], "isController": false}, {"data": [1.0, 500, 1500, "Create topic"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.5, 500, 1500, "Create question 1"], "isController": false}, {"data": [0.5, 500, 1500, "Create question 4"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of topics"], "isController": false}, {"data": [1.0, 500, 1500, "Add topic to question 5"], "isController": false}, {"data": [0.5, 500, 1500, "Create question 5"], "isController": false}, {"data": [1.0, 500, 1500, "Create question 2"], "isController": false}, {"data": [1.0, 500, 1500, "Create question 3"], "isController": false}, {"data": [0.004, 500, 1500, "Login as student"], "isController": false}, {"data": [1.0, 500, 1500, "Add topic to question 1"], "isController": false}, {"data": [1.0, 500, 1500, "Add topic to question 2"], "isController": false}, {"data": [1.0, 500, 1500, "Add topic to question 3"], "isController": false}, {"data": [1.0, 500, 1500, "Add topic to question 4"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Generate quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 686, 250, 36.44314868804665, 23064.588921282797, 207, 60289, 41275.9, 46006.7, 51538.28, 9.552321938313723, 19.504964470340457, 2.7599534176355913], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of assessments", 1, 0, 0.0, 386.0, 386, 386, 386.0, 386.0, 386.0, 2.5906735751295336, 2.4211666126943006, 2.0366135038860103], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 1489.0, 1489, 1489, 1489.0, 1489.0, 1489.0, 0.671591672263264, 5.45405893216924, 0.5240251427132303], "isController": false}, {"data": ["Create assessment", 1, 0, 0.0, 308.0, 308, 308, 308.0, 308.0, 308.0, 3.246753246753247, 2.279702719155844, 3.3450436282467533], "isController": false}, {"data": ["Create topic", 1, 0, 0.0, 340.0, 340, 340, 340.0, 340.0, 340.0, 2.941176470588235, 1.8238740808823528, 2.455767463235294], "isController": false}, {"data": ["Start quiz", 167, 164, 98.20359281437126, 13412.407185628745, 207, 41870, 23527.400000000005, 27673.999999999993, 41490.56, 3.851920193749279, 13.153714609041634, 0.053991934321300894], "isController": false}, {"data": ["Create question 1", 1, 0, 0.0, 550.0, 550, 550, 550.0, 550.0, 550.0, 1.8181818181818181, 1.5039062499999998, 2.3277698863636362], "isController": false}, {"data": ["Create question 4", 1, 0, 0.0, 505.0, 505, 505, 505.0, 505.0, 505.0, 1.9801980198019802, 1.637917698019802, 2.5351949257425743], "isController": false}, {"data": ["Get list of topics", 1, 0, 0.0, 430.0, 430, 430, 430.0, 430.0, 430.0, 2.3255813953488373, 1.8713662790697674, 1.8077761627906976], "isController": false}, {"data": ["Add topic to question 5", 1, 0, 0.0, 413.0, 413, 413, 413.0, 413.0, 413.0, 2.4213075060532687, 1.073509382566586, 2.033519975786925], "isController": false}, {"data": ["Create question 5", 1, 0, 0.0, 600.0, 600, 600, 600.0, 600.0, 600.0, 1.6666666666666667, 1.3802083333333335, 2.1337890625], "isController": false}, {"data": ["Create question 2", 1, 0, 0.0, 458.0, 458, 458, 458.0, 458.0, 458.0, 2.1834061135371177, 1.8081331877729256, 2.795356850436681], "isController": false}, {"data": ["Create question 3", 1, 0, 0.0, 485.0, 485, 485, 485.0, 485.0, 485.0, 2.061855670103093, 1.7074742268041239, 2.6397390463917527], "isController": false}, {"data": ["Login as student", 250, 0, 0.0, 16174.339999999997, 550, 40921, 25386.4, 29778.699999999997, 35638.13000000001, 6.068403039056242, 8.661033418695535, 0.9185571006383959], "isController": false}, {"data": ["Add topic to question 1", 1, 0, 0.0, 338.0, 338, 338, 338.0, 338.0, 338.0, 2.9585798816568047, 1.3117141272189348, 2.484744822485207], "isController": false}, {"data": ["Add topic to question 2", 1, 0, 0.0, 327.0, 327, 327, 327.0, 327.0, 327.0, 3.058103975535168, 1.3558390672782874, 2.568329510703364], "isController": false}, {"data": ["Add topic to question 3", 1, 0, 0.0, 291.0, 291, 291, 291.0, 291.0, 291.0, 3.4364261168384878, 1.5235717353951892, 2.886060996563574], "isController": false}, {"data": ["Add topic to question 4", 1, 0, 0.0, 282.0, 282, 282, 282.0, 282.0, 282.0, 3.5460992907801416, 1.5721963652482271, 2.978169326241135], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 601.0, 601, 601, 601.0, 601.0, 601.0, 1.663893510815308, 2.2261075291181367, 0.25185888103161397], "isController": false}, {"data": ["Generate quiz", 250, 83, 33.2, 38089.2, 18613, 60289, 47623.7, 49940.44999999999, 55993.10000000005, 3.943279862458398, 7.009749882687424, 2.2565419012918184], "isController": false}, {"data": ["Submit answer 1", 3, 3, 100.0, 2916.0, 1113, 4145, 4145.0, 4145.0, 4145.0, 0.7045561296383278, 2.4363606006341003, 0.0], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["504\/Gateway Time-out", 1, 0.4, 0.1457725947521866], "isController": false}, {"data": ["Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 249, 99.6, 36.29737609329446], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 686, 250, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 249, "504\/Gateway Time-out", 1, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Start quiz", 167, 164, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 164, null, null, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Generate quiz", 250, 83, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 82, "504\/Gateway Time-out", 1, null, null, null, null, null, null], "isController": false}, {"data": ["Submit answer 1", 3, 3, "Non HTTP response code: javax.net.ssl.SSLException\/Non HTTP response message: Socket closed", 3, null, null, null, null, null, null, null, null], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
