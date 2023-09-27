import json
import argparse
from collections import defaultdict
import plotly.graph_objs as go
from plotly.subplots import make_subplots

if __name__ == "__main__":
    # Set up argument parsing
    parser = argparse.ArgumentParser(description='Plot metrics over game runs.')
    parser.add_argument('--metric-filter', dest='metric_filter', default='discarded-cards', help='Metric filter to visualize (default: discarded-cards)')
    parser.add_argument('--metric-measure', dest='metric_measure', default='count', help='Metric measurement to visualize (default: count)')

    # Parse the arguments
    args = parser.parse_args()
    metric_filter = args.metric_filter
    metric_measure = args.metric_measure

print(f"Plotting {metric_filter} {metric_measure} over game runs...")

with open('./statistics-per-game.json', 'r') as file:
    logs = json.load(file)

# Extract city name function
def get_city_name(stat_name):
    return stat_name.split('-')[0]

# Group data by city
city_data = defaultdict(lambda: defaultdict(dict))

for log in logs:
    for stat in log["statistics"]:
        if metric_filter not in stat["statisticName"]:
            continue
        city = get_city_name(stat["statisticName"])
        replication = stat["replicationNumber"]
        city_data[city][stat["statisticName"]][replication] = stat[metric_measure]

# Compute max_runs for the entire dataset
max_runs = max([max(values.keys()) for metrics in city_data.values() for values in metrics.values()])

# Create a subplot for each city
fig = make_subplots(rows=len(city_data), cols=1, subplot_titles=list(city_data.keys()))

row = 1
for city, city_metrics in city_data.items():
    x_values = list(range(1, max_runs + 1))
    for metric, values in city_metrics.items():
        y_values = [values.get(i, None) for i in x_values]
        legend_name = city + '-' + '-'.join(metric.split('-')[1:])
        fig.add_trace(
            go.Scatter(x=x_values, y=y_values, mode='lines', name=legend_name),
            row=row,
            col=1
        )
    row += 1

# Update layout for better visuals
fig.update_layout(
    height=300*len(city_data),
    title_text="Metrics over Game Runs",
    showlegend=True,
    updatemenus=[{
        'buttons': [
            {
                'args': [None, {'frame': {'duration': 500, 'redraw': True}, 'fromcurrent': True}],
                'label': 'Play',
                'method': 'animate'
            },
            {
                'args': [[None], {'frame': {'duration': 0, 'redraw': True}, 'mode': 'immediate', 'transition': {'duration': 0}}],
                'label': 'Pause',
                'method': 'animate'
            }
        ],
        'direction': 'left',
        'pad': {'r': 10, 't': 87},
        'showactive': False,
        'type': 'buttons',
        'x': 0.1,
        'xanchor': 'right',
        'y': 0,
        'yanchor': 'top'
    }],
    sliders=[{
        'yanchor': 'top',
        'xanchor': 'left',
        'currentvalue': {
            'font': {'size': 20},
            'prefix': 'Game Run:',
            'visible': True,
            'xanchor': 'right'
        }
    }],
)

fig.update_xaxes(title_text="Game Runs")
fig.update_yaxes(title_text=metric_measure.capitalize())

fig.show()
