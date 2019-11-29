/** @format */

const path = require("path");
const SizePlugin = require("size-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const PrerenderSpaPlugin = require("prerender-spa-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const isProductionEnvFlag = process.env.NODE_ENV === "production";

module.exports = {
  publicPath: "/",
  outputDir: "dist",
  lintOnSave: true,
  runtimeCompiler: false,

  // generate sourceMap for production build?
  productionSourceMap: process.env.NODE_ENV !== "production",

  chainWebpack: config => {
    config.resolve.alias
      .set("vue$", "vue/dist/vue.esm.js")
      .set("@assets", path.join(__dirname, "src/assets"))
      .set("@models", path.join(__dirname, "src/models"))
      .set("@plugins", path.join(__dirname, "src/plugins"))
      .set("@services", path.join(__dirname, "src/services"))
      .set("@styles", path.join(__dirname, "src/styles"))
      .set("@views", path.join(__dirname, "src/views"));

    const splitOptions = config.optimization.get("splitChunks");
    config.optimization.splitChunks(
      Object.assign({}, splitOptions, {
        maxAsyncRequests: 16,
        maxInitialRequests: 16,
        minChunks: 1,
        minSize: 30000,
        automaticNameDelimiter: "~",
        name: true,
        cacheGroups: {
          default: false,
          common: {
            name: `chunk-common`,
            minChunks: 2,
            priority: -20,
            chunks: "initial",
            reuseExistingChunk: true
          },
          element: {
            name: "element",
            test: /[\\/]node_modules[\\/]element-ui[\\/]/,
            chunks: "initial",
            priority: -30
          }
        }
      })
    );

    // https://github.com/webpack-contrib/webpack-bundle-analyzer
    if (process.env.npm_config_report) {
      config
        .plugin("webpack-bundle-analyzer")
        .use(require("webpack-bundle-analyzer").BundleAnalyzerPlugin);
    }
  },

  configureWebpack: {
    plugins: [
      isProductionEnvFlag
        ? new PrerenderSpaPlugin({
            // Required - The path to the webpack-outputted app to prerender.
            staticDir: path.join(__dirname, "dist"),
            // Required - Routes to render.
            routes: ["/", "/explore"]
          })
        : () => {},
      isProductionEnvFlag ? new SizePlugin() : () => {},
      isProductionEnvFlag ? new CompressionPlugin() : () => {},
      isProductionEnvFlag ? new TerserPlugin() : () => {}
    ]
  },

  // use thread-loader for babel & TS in production build
  // enabled by default if the machine has more than 1 cores
  parallel: require("os").cpus().length > 1,

  // options for the PWA plugin.
  // see => https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-pwa
  // https://developers.google.com/web/tools/workbox/modules/workbox-webpack-plugin
  pwa: {
    name: "Vue-Cli3 实践",
    themeColor: "#4DBA87",
    msTileColor: "#000000",
    appleMobileWebAppCapable: "yes",
    appleMobileWebAppStatusBarStyle: "black",
    iconPaths: {
      favicon32: "img/icons/fuji-mountain-32x32.png",
      favicon16: "img/icons/fuji-mountain-16x16.png",
      appleTouchIcon: "img/icons/apple-touch-icon-152x152.png",
      maskIcon: "img/icons/safari-pinned-tab.svg",
      msTileImage: "img/icons/msapplication-icon-144x144.png"
    },
    // configure the workbox plugin (GenerateSW or InjectManifest)
    workboxPluginMode: "InjectManifest",
    workboxOptions: {
      // swSrc is required in InjectManifest mode.
      swSrc: "public/service-worker.js"
      // ...other Workbox options...
    }
  },

  // configure webpack-dev-server behavior
  devServer: {
    open: process.platform === "darwin",
    host: "0.0.0.0",
    port: 8081,
    https: false,
    hotOnly: false,
    disableHostCheck: true,
    proxy: null, // string | Object
    before: app => {}
  },

  // options for 3rd party plugins
  pluginOptions: {}
};
