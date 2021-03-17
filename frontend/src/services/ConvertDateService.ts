export function ISOtoString(dateString: string | null): string {
  if (dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const min = date.getMinutes();

    const monthString = month < 10 ? '0' + month : '' + month;
    const dayString = day < 10 ? '0' + day : '' + day;
    const hourString = hour < 10 ? '0' + hour : '' + hour;
    const minString = min < 10 ? '0' + min : '' + min;

    return `${year}-${monthString}-${dayString} ${hourString}:${minString}`;
  } else {
    return '-';
  }
}

export function milisecondsToHHMMSS(
  miliseconds: number | undefined | null
): string {
  if (miliseconds) {
    const timeInSeconds = Math.abs(miliseconds) / 1000;
    const hours = Math.floor(timeInSeconds / 3600);
    const minutes = Math.floor((timeInSeconds - hours * 3600) / 60);
    const seconds = Math.floor(timeInSeconds - hours * 3600 - minutes * 60);

    const hoursString = hours < 10 ? '0' + hours : hours;
    const minutesString = minutes < 10 ? '0' + minutes : minutes;
    const secondsString = seconds < 10 ? '0' + seconds : seconds;

    return (
      (miliseconds > 0 ? '' : '-') +
      `${hoursString}:${minutesString}:${secondsString}`
    );
  }
  return '';
}
