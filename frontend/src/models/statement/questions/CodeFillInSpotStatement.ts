import StatementOption from '@/models/statement/StatementOption';

function shuffle(array: any[]) {
  let currentIndex = array.length, randomIndex;
  while (currentIndex != 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;
    [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
  }
  return array;
}

export default class StatementFillInSpot {
  sequence!: number;
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementFillInSpot) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence || this.sequence;
      this.options = jsonObj.options
        ? shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        )
        : this.options;
    }
  }
}
