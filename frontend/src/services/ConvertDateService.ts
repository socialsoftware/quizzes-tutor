export function ISOtoString(dateString: string | null): string {
  if (dateString) {
    let date = new Date(dateString);
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let min = date.getMinutes();

    let monthString = month < 10 ? '0' + month : '' + month;
    let dayString = day < 10 ? '0' + day : '' + day;
    let hourString = hour < 10 ? '0' + hour : '' + hour;
    let minString = min < 10 ? '0' + min : '' + min;

    return `${year}-${monthString}-${dayString} ${hourString}:${minString}`;
  } else {
    return '-';
  }
}

export function milisecondsToHHMMSS(
  miliseconds: number | undefined | null
): string {
  if (miliseconds) {
    let timeInSeconds = Math.abs(miliseconds) / 1000;
    let hours = Math.floor(timeInSeconds / 3600);
    let minutes = Math.floor((timeInSeconds - hours * 3600) / 60);
    let seconds = Math.floor(timeInSeconds - hours * 3600 - minutes * 60);

    let hoursString = hours < 10 ? '0' + hours : hours;
    let minutesString = minutes < 10 ? '0' + minutes : minutes;
    let secondsString = seconds < 10 ? '0' + seconds : seconds;

    return (
      (miliseconds > 0 ? '' : '-') +
      `${hoursString}:${minutesString}:${secondsString}`
    );
  }
  return '';
}
