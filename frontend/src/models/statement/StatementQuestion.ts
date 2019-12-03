import StatementOption from "@/models/statement/StatementOption";
import Image from "@/models/management/Image";

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;

  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;

      if (jsonObj.options) {
        import("vue-underscore").then(_ => {
          this.options = _.shuffle(
            jsonObj.options.map(
              (option: StatementOption) => new StatementOption(option)
            )
          );
        });
      }
    }
  }
}
