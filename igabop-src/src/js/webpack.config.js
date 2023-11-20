const path = require('path');

const HtmlWebPackPlugin = require('html-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

const devHtml = new HtmlWebPackPlugin({
  template: './src/production.html',
  filename: './widget.html',
  inject: 'head',
  hash: true,
  cache: false
});

const getDevServer = () => {
  try {
    return require('../../config.json').devServer;
  } catch (e) {
    console.warn('config.json was not found');
    return {};
  }
};

module.exports = {
  node: {
    fs: 'empty'
  },
  devtool: 'eval',
  entry: path.resolve(__dirname, `./src/index.ts`),
  module: {
    rules: [
      {
        test: /\.html$/,
        use: 'html-loader'
      },
      {
        test: /\.m?js$/,
        exclude: /node_modules/,
        use: ['thread-loader', 'babel-loader']
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.scss$/i,
        use: ['style-loader', 'css-loader', 'sass-loader']
      },
      {
        test: /\.jsx$/,
        exclude: /node_modules/,
        use: [
          'thread-loader',
          {
            loader: 'babel-loader',
            options: {
              presets: [
                [
                  '@babel/preset-env',
                  {
                    targets: {
                      node: true
                    }
                  }
                ],
                '@babel/preset-react'
              ]
            }
          }
        ]
      },
      {
        // test: /\.(png|jpe?g|gif|woff|woff2|ttf)$/i,
        // loader: 'url-loader?name=[name].[ext]',
        // options: {
        //   esModule: false
        // }
        test: /\.(png|jpe?g|gif|woff|woff2|ttf)$/i,
        loader: 'file-loader',
        options: {
          esModule: false,
          name: '[name].[ext]'
        }
      },
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: [
          'thread-loader',
          'babel-loader',
          {
            loader: 'ts-loader',
            options: {
              transpileOnly: true,
              happyPackMode: true
            }
          }
        ]
      },
      {
        test: /\.svg$/,
        use: { loader: 'svg-url-loader' }
      }
    ]
  },
  resolve: {
    extensions: ['.js', '.jsx', '.css', '.ts', '.tsx']
  },
  plugins: [
    devHtml,
    new ForkTsCheckerWebpackPlugin({
      typescript: {
        configFile: path.resolve(__dirname, './tsconfig.json'),
        enabled: true,
        memoryLimit: 4096,
        diagnosticOptions: {
          declaration: true,
          global: true,
          semantic: true,
          syntactic: true
        }
      }
    })
  ],
  devServer: {
    publicPath: `/`,
    host: '0.0.0.0',
    ...getDevServer(),
    historyApiFallback: {
      index: 'widget.html'
    },
    compress: true,
    disableHostCheck: true,
    port: 8081
  },
  optimization: {
    minimize: true,
    minimizer: [new TerserPlugin()]
  }
};
