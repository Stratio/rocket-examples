

## Plugins

- rocket-1.0.0-SDK  
    - Custom Input: GeneratorXDLiteInputStepBatch & GeneratorXDLiteInputStepStreaming
    - Custom Transform: RepartitionXDLiteTransformStepBatch & RepartitionXDLiteTransformStepStreaming
    - Custom Transform: TokenizerTransformStepBatch & TokenizerTransformStepStreaming
    - Custom Output: LoggerXDLiteOutputStep

- rocket-1.1.0-SDK      
    - New functionality: metadata management 
    - New step: MetadataTestXDLiteInputStepBatch

- rocket-2.2.0-SDK  
    - New functionality: Execution report logs in custom steps
    - New steps: ReportLogTestXDLiteInputStepBatch & ReportLogTestXDLiteInputStepStreaming 
    - Added reporting in:  
        - GeneratorXDLiteInputStepBatch & GeneratorXDLiteInputStepStreaming
        - TokenizerTransformStepBatch
        - LoggerXDLiteOutputStep