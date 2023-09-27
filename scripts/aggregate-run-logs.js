import fs from 'fs';

let logDirectory = './run-log';
var output = fs.readdirSync(logDirectory).filter(file => file.match('R.*.0.json')).map((file) => {
    const parsedLines = fs.readFileSync(`${logDirectory}/${file}`)
        .toString()
        .split('\n')
        .filter(line => line.length > 0)
        .map(JSON.parse);

    return {
        filename: file,
        // logs: parsedLines.filter(l => l['statisticName'] === undefined),
        statistics: parsedLines.filter(l => l['statisticName'] !== undefined)
    }
});

fs.writeFileSync(`./metrics-animation/statistics-per-game.json`, JSON.stringify(output, null, 2));