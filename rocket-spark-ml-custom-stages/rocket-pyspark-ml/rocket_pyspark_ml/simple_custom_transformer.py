from pyspark import keyword_only
from pyspark.ml import Transformer
from pyspark.ml.param.shared import *
from pyspark.ml.util import DefaultParamsReadable, DefaultParamsWritable
from pyspark.sql import DataFrame
from pyspark.sql.functions import lit


class HasLiteralValue(Params):
    literalValue = Param(
        Params._dummy(), "literalValue", "literalValue", typeConverter=TypeConverters.toFloat
    )

    def __init__(self):
        super(HasLiteralValue, self).__init__()
        self._setDefault(literalValue=1.0)

    def setLiteralValue(self, value):
        return self._set(literalValue=value)

    def getLiteralValue(self):
        return self.getOrDefault(self.literalValue)


class LiteralColumnAdder(Transformer, HasLiteralValue, HasOutputCol, DefaultParamsReadable, DefaultParamsWritable):
    """
    A custom Transformer which drops all columns that have at least one of the
    words from the banned_list in the name.
    """

    @keyword_only
    def __init__(self):
        super(LiteralColumnAdder, self).__init__()

    def _transform(self, df: DataFrame) -> DataFrame:
        return df.withColumn(self.getOutputCol(), lit(self.getLiteralValue()))
