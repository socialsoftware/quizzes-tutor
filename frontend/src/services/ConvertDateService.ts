export function ISOtoString(dateString: string | undefined | null): string {
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
