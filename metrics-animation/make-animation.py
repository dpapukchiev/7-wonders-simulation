import json
from collections import defaultdict
import matplotlib.pyplot as plt
import matplotlib.animation as animation

with open('./statistics-per-game.json', 'r') as file:
    logs = json.load(file)

# Extract city name function
def get_city_name(stat_name):
    return stat_name.split('-')[0]

# Group data by city
city_data = defaultdict(lambda: defaultdict(lambda: defaultdict(list)))

for log in logs:
    for stat in log["statistics"]:
        if "discarded-cards" not in stat["statisticName"]:
            continue
        city = get_city_name(stat["statisticName"])
        city_data[city][stat["statisticName"]]["count"].append(stat["count"])

metric_name = "RHODOS-B-discarded-DEFAULT"
data_values = city_data[get_city_name(metric_name)][metric_name]["count"]

for idx, value in enumerate(data_values):
    print(f"Game {idx + 1}: {value}")

# Compute max_runs for the entire dataset
max_runs = max([len(data["count"]) for metrics in city_data.values() for data in metrics.values()])

# Create the animation
fig, ax = plt.subplots()

def init():
    ax.clear()
    ax.set_title(f'Metrics over Game Runs')
    ax.set_xlabel('Game Runs')
    ax.set_ylabel('Count')
    ax.set_xlim(0, max_runs)  # setting consistent x-axis limits
    return ax,

def animate(i):
    ax.clear()
    for city, city_metrics in city_data.items():
        for metric, values in city_metrics.items():
            legend_name = city + '-' + '-'.join(metric.split('-')[1:])  # Include city in the metric name for legend
            ax.plot(values["count"][:i], label=legend_name)

    # Set x-ticks to show up at regular intervals, e.g., every 50 game runs
    ax.set_xticks(list(range(0, max_runs + 1, max_runs//20)))  # setting 20 ticks across the dataset
    ax.legend(loc="upper right", fontsize='small', bbox_to_anchor=(1.05, 1), borderaxespad=0.)
    return ax,

ani = animation.FuncAnimation(fig, animate, frames=max_runs, init_func=init, blit=True, interval=50)  # Setting the interval to 50ms

plt.tight_layout()
plt.show()
