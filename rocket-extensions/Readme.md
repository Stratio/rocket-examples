# Rocket extensions

## UDF

// TODO

## Plugins

- Legacy versions (Documentation in branches older than 9.5 links it)  
    - /input-lite-xd  
    - /output-lite-xd  
    - /transformation-lite-xd

**Note: plugins generated with new SDK versions must be included in new folders:**

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
        - TokenizerTransformStepBatch & TokenizerTransformStepStreaming
        - LoggerXDLiteOutputStep