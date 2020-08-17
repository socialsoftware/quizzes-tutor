export default class Image {
    constructor(jsonObj) {
        this.width = null;
        if (jsonObj) {
            this.url = jsonObj.url;
            this.width = jsonObj.width;
        }
    }
}
//# sourceMappingURL=Image.js.map