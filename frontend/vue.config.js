module.exports = {
  // ...
  devServer: {
    open: process.platform === "darwin",
    host: "0.0.0.0",
    port: 8081,
    https: false,
    hotOnly: false,
    disableHostCheck: true
  }
  // ...
};
