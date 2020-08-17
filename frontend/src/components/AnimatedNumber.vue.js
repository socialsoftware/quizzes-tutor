import { __decorate } from "tslib";
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
let AnimatedNumber = class AnimatedNumber extends Vue {
    constructor() {
        super(...arguments);
        this.displayNumber = 0;
        this.interval = 0;
    }
    created() {
        this.updateNumber();
    }
    updateNumber() {
        clearInterval(0);
        if (this.number == this.displayNumber) {
            return;
        }
        this.interval = window.setInterval(() => {
            if (this.displayNumber < this.number) {
                let change = (this.number - this.displayNumber) / 10;
                change = change >= 0 ? Math.ceil(change) : Math.floor(change);
                this.displayNumber = this.displayNumber + change;
            }
        }, 20);
    }
};
__decorate([
    Prop({ default: 0 })
], AnimatedNumber.prototype, "number", void 0);
__decorate([
    Watch('number')
], AnimatedNumber.prototype, "updateNumber", null);
AnimatedNumber = __decorate([
    Component
], AnimatedNumber);
export default AnimatedNumber;
//# sourceMappingURL=AnimatedNumber.vue.js.map