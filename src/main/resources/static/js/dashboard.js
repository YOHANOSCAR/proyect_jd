/* dashboard.js */

document.addEventListener("DOMContentLoaded", function() {

    // ------------------------------------------------------------------
    // 1) Cantidades vendidas (semana) - sin cambios
    var mapCantidadesSemanal = /*[[${cantidadesVendidasSemanal}]]*/ {};
    var labelsCantSem = Object.keys(mapCantidadesSemanal);
    var dataCantSem = Object.values(mapCantidadesSemanal);

    const ctxCantSem = document.getElementById('chartCantidadesSemana');
    new Chart(ctxCantSem, {
        type: 'bar',
        data: {
            labels: labelsCantSem,
            datasets: [{
                label: 'Productos Vendidos (Semana)',
                data: dataCantSem,
                backgroundColor: 'rgba(54, 162, 235, 0.7)'
            }]
        }
        // Puedes añadir opciones si quieres: options: {...}
    });


    // ------------------------------------------------------------------
    // 2) GANANCIAS: DOUGHNUT con select "Semanal" / "Mensual"
    var mapGananciasSem = /*[[${gananciasSemanales}]]*/ {};
    var mapGananciasMes = /*[[${gananciasMensuales}]]*/ {};

    // Consolidar en un objeto dataGanancias
    var dataGanancias = {
        semanal: mapGananciasSem,
        mensual: mapGananciasMes
    };

    // Función auxiliar para convertir map => array => ordenar => {labels, values}
    function getSortedData(mapObject) {
        let entries = Object.entries(mapObject);
        // Ordenar desc por valor
        entries.sort((a, b) => b[1] - a[1]);
        return {
            labels: entries.map(e => e[0]),
            values: entries.map(e => e[1])
        };
    }

    // Iniciar con "semanal"
    let inicialGan = getSortedData(dataGanancias.semanal);

    const ctxGan = document.getElementById('chartGanancias');
    const chartGanancias = new Chart(ctxGan, {
        type: 'doughnut', // <--- Tipo DOUGHNUT
        data: {
            labels: inicialGan.labels,
            datasets: [{
                label: 'Ganancias',
                data: inicialGan.values,
                backgroundColor: [
                    // Colores para cada sector
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(54, 162, 235, 0.7)',
                    'rgba(255, 206, 86, 0.7)',
                    'rgba(75, 192, 192, 0.7)',
                    'rgba(153, 102, 255, 0.7)',
                    'rgba(255, 159, 64, 0.7)'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });

    // Al cambiar el select #filtroGanancias => "semanal" o "mensual"
    document.getElementById('filtroGanancias').addEventListener('change', function(e) {
        let periodo = e.target.value; // 'semanal' | 'mensual'
        let sorted = getSortedData(dataGanancias[periodo]);

        chartGanancias.data.labels = sorted.labels;
        chartGanancias.data.datasets[0].data = sorted.values;
        chartGanancias.update();
    });


    // ------------------------------------------------------------------
    // 3) Ventas Semanales (importe total) - sin cambios
    var mapVentasSem = /*[[${ventasPorSemana}]]*/ {};
    var labelsVSem = Object.keys(mapVentasSem);
    var dataVSem = Object.values(mapVentasSem);

    const ctxVSem = document.getElementById('chartVentasSemanales');
    new Chart(ctxVSem, {
        type: 'bar',
        data: {
            labels: labelsVSem,
            datasets: [{
                label: 'Ventas Semanales (importe)',
                data: dataVSem,
                backgroundColor: 'rgba(75, 192, 192, 0.7)'
            }]
        }
        // Igualmente, podrías agregar options: {...}
    });

});
