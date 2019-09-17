// To allow gzip compression
const CompressionPlugin = require("compression-webpack-plugin");
const VuetifyLoaderPlugin = require("vuetify-loader/lib/plugin");

module.exports = {
  plugins: [new CompressionPlugin(), new VuetifyLoaderPlugin()]
};
