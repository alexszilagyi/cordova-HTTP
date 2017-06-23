/*global angular*/

/*
 * An HTTP Plugin for PhoneGap.
 */

var PluginLoader = function (require, exports, module) {

    var exec = require('cordova/exec');

    var http = {
        useBasicAuth: function(username, password, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "useBasicAuth", [username, password]);
        },
        setHeader: function(header, value, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "setHeader", [header, value]);
        },
        enableSSLPinning: function(enable, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "enableSSLPinning", [enable]);
        },
        acceptAllCerts: function(allow, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "acceptAllCerts", [allow]);
        },
        post: function(url, params, headers, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "post", [url, params, headers]);
        },
        postJson: function(url, jsonObj, headers, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "postJson", [url, jsonObj, headers]);
        },
        get: function(url, params, headers, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "get", [url, params, headers]);
        },
        uploadFile: function(url, params, headers, filePath, name, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "uploadFile", [url, params, headers, filePath, name]);
        },
        downloadFile: function(url, params, headers, filePath, success, failure) {
            /*
             *
             * Licensed to the Apache Software Foundation (ASF) under one
             * or more contributor license agreements.  See the NOTICE file
             * distributed with this work for additional information
             * regarding copyright ownership.  The ASF licenses this file
             * to you under the Apache License, Version 2.0 (the
             * "License"); you may not use this file except in compliance
             * with the License.  You may obtain a copy of the License at
             *
             *   http://www.apache.org/licenses/LICENSE-2.0
             *
             * Unless required by applicable law or agreed to in writing,
             * software distributed under the License is distributed on an
             * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
             * KIND, either express or implied.  See the License for the
             * specific language governing permissions and limitations
             * under the License.
             *
             * Modified by Andrew Stephan for Sync OnSet
             *
            */
            var win = function(result) {
                var entry = new (require('org.apache.cordova.file.FileEntry'))();
                entry.isDirectory = false;
                entry.isFile = true;
                entry.name = result.file.name;
                entry.fullPath = result.file.fullPath;
                success(entry);
            };
            return exec(win, failure, "CordovaHttpPlugin", "downloadFile", [url, params, headers, filePath]);
        },
        invalidateSessionCancelingTasks: function(cancelPendingTasks, success, failure) {
            return exec(success, failure, "CordovaHttpPlugin", "invalidateSessionCancelingTasks", [cancelPendingTasks]);
        }
    };

    module.exports = http;

    window.cordovaHTTP = http;
}

PluginLoader(require, exports, module);

cordova.define("cordova/plugin/cordovaHTTP", PluginLoader);
