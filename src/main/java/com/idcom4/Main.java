package com.idcom4;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.AddressSpace;
import com.idcom4.compiler.context.Context;
import com.idcom4.exceptions.*;
import com.idcom4.infra.Options;
import com.idcom4.infra.OptionsManager;
import com.idcom4.infra.OptionsPrinter;
import com.idcom4.utils.ByteUtils;
import com.idcom4.utils.FileUtils;

public class Main {
    public static void main(String[] args) throws IDException {

        // get options
        Options options = OptionsManager.GetOptions(args);
        if (options.GetHelp()) {
            OptionsPrinter.PrintAvailableOptions();
            return ;
        }

        // validate options
        if (options.getIdASMFile() == null)
            throw new IDException("no idasm file provided");
        String sourceCode = FileUtils.ReadFile(options.getIdASMFile());

        // init context
        Context.InitInstance(new AddressSpace((short) 0));

        // compile source code
        IdASMCompiler compiler = new IdASMCompiler();
        byte[] bytes = compiler.Compile(sourceCode);

        // generate xmem file from bytes
        String xmemFileContent = ByteUtils.BytesToXmemString(bytes, Context.INSTANCE.byteEncoding);

        // save xmem to file
        FileUtils.WriteFile(options.GetOutputFile() + ".xmem", xmemFileContent);
    }


}
