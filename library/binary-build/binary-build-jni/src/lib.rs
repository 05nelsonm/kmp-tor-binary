/*
 * Copyright (c) 2022 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#![allow(non_snake_case)]

extern crate tor_sys;

use jni::JNIEnv;
use jni::objects::{JClass, JList, JString, JObject};
use std::ffi::{CString, c_void};
use jni::sys::jstring;

/*
 * Success: Returns empty jstring
 * Error: Returns non-empty jstring (error message to throw TorManagerException)
 **/
#[no_mangle]
pub extern "system" fn Java_io_matthewnelson_kmp_tor_KmpTorLoaderJni_runLines(
    env: JNIEnv,
    _class: JClass,
    // lines are a list of of strings to be run, already configured from kotlin side:
    // ex: [ "--ControlPortWriteToFile", "/path/to/file" ]
    lines: JObject // JList
) -> jstring {
    let mut verify: Vec<String> = vec![
        String::from("tor"), String::from("--verify-config"),
    ];

    let mut run: Vec<String> = vec![
        String::from("tor"),
    ];

    let lines: JList = env.get_list(lines).expect("expected List<String>").into();
    for i in 0..lines.size().unwrap() {
        let line = JString::from(lines.get(i.into()).expect("expected a String").unwrap());
        verify.push(env.get_string(line.clone()).unwrap().into());
        run.push(env.get_string(line.clone()).unwrap().into());
    }

    let verify: Vec<_> = verify.into_iter().map(|s| CString::new(s).unwrap()).collect();
    let verify: Vec<_> = verify.iter().map(|s| s.as_ptr()).collect();

    let run: Vec<_> = run.into_iter().map(|s| CString::new(s).unwrap()).collect();
    let run: Vec<_> = run.iter().map(|s| s.as_ptr()).collect();

    let config = unsafe {
        tor_sys::tor_main_configuration_new()
    };

    if config.is_null() {
        return env.new_string("TorConfig was null.").unwrap().into_inner();
    }

    let result = unsafe {
        // set lines to be verified
        tor_sys::tor_main_configuration_set_command_line(
            config,
            verify.len() as i32,
            verify.as_ptr()
        )
    };

    if result != 0 {
        return prepare_err_msg(config, &env, "Failed to set args for verification.");
    }

    let result = unsafe {
        // run lines to verify
        tor_sys::tor_run_main(config)
    };

    if result != 0 {
        return prepare_err_msg(config, &env, "Failed to verify args. Tor does not like a setting.");
    }

    let result = unsafe {
        // set lines to be run
        tor_sys::tor_main_configuration_set_command_line(
            config,
            run.len() as i32,
            run.as_ptr()
        )
    };

    if result != 0 {
        return prepare_err_msg(config, &env, "Failed to set args to run.");
    }

    let result = unsafe {
        // run lines
        // This is runBlocking on the current Thread (which is handled kotlin side)
        tor_sys::tor_run_main(config)
    };

    if result != 0 {
        return prepare_err_msg(config, &env, "Failed to run args.");
    }

    unsafe {
        // release config from memory
        tor_sys::tor_main_configuration_free(config);
    }

    return env.new_string("").unwrap().into_inner();
}

fn prepare_err_msg(config: *mut c_void, env: &JNIEnv, msg: &str) -> jstring {
    unsafe {
        tor_sys::tor_main_configuration_free(config)
    };

    return env.new_string(msg).unwrap().into_inner();
}
